package com.legal.assistant.module.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.assistant.module.ai.dto.ChatRequest;
import com.legal.assistant.module.ai.dto.ChatResponse;
import com.legal.assistant.module.ai.service.AiService;
import com.legal.assistant.module.rag.service.LegalRagService;
import com.legal.assistant.module.search.service.WebSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MiniMaxAiServiceImpl implements AiService {

    @Value("${ai.enabled:false}")
    private boolean aiEnabled;

    @Value("${ai.base-url:https://api.minimax.io/v1}")
    private String baseUrl;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:MiniMax-M2.7}")
    private String model;

    @Value("${ai.max-tokens:2048}")
    private int maxTokens;

    @Value("${ai.temperature:0.7}")
    private double temperature;

    @Value("${ai.rag.auto-retrieve:true}")
    private boolean autoRetrieveRag;

    private final HttpClient httpClient;
    private final WebSearchService searchService;
    private final LegalRagService ragService;

    public MiniMaxAiServiceImpl(WebSearchService searchService, LegalRagService ragService) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.searchService = searchService;
        this.ragService = ragService;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        StringBuilder userPromptBuilder = new StringBuilder();
        userPromptBuilder.append(request.getMessage());

        boolean hasWebSearch = false;
        StringBuilder webSearchResults = new StringBuilder();

        try {
            var webResults = searchService.search(request.getMessage(), 5);
            if (!webResults.isEmpty()) {
                hasWebSearch = true;
                webSearchResults.append("\n\n=== 网络搜索结果 ===");
                for (int i = 0; i < webResults.size(); i++) {
                    var result = webResults.get(i);
                    webSearchResults.append("\n").append(i + 1).append(". ").append(result.getTitle());
                    webSearchResults.append("\n   来源：").append(result.getUrl());
                    if (!result.getDescription().isEmpty()) {
                        webSearchResults.append("\n   摘要：").append(result.getDescription());
                    }
                    webSearchResults.append("\n");
                }
            }
        } catch (Exception e) {
            log.warn("Web search failed, continuing without search results: {}", e.getMessage());
        }

        if (hasWebSearch) {
            userPromptBuilder.append(webSearchResults.toString());
            userPromptBuilder.append("\n\n请根据以上网络搜索结果，用中文详细回答用户问题。回答要专业、实用，优先引用搜索到的来源。");
        }

        boolean needsAutoRag = autoRetrieveRag &&
                (request.getLawSources() == null || request.getLawSources().isEmpty()) &&
                (request.getCaseSources() == null || request.getCaseSources().isEmpty());

        if (needsAutoRag) {
            try {
                Map<String, Object> ragContext = ragService.retrieveContext(request.getMessage(), 3, 2);
                @SuppressWarnings("unchecked")
                List<Map<String, String>> lawSources = (List<Map<String, String>>) ragContext.get("lawSources");
                @SuppressWarnings("unchecked")
                List<Map<String, String>> caseSources = (List<Map<String, String>>) ragContext.get("caseSources");
                if (lawSources != null && !lawSources.isEmpty()) {
                    request.setLawSources(lawSources);
                }
                if (caseSources != null && !caseSources.isEmpty()) {
                    request.setCaseSources(caseSources);
                }
                log.info("RAG auto-retrieved: {} laws, {} cases", lawSources != null ? lawSources.size() : 0, caseSources != null ? caseSources.size() : 0);
            } catch (Exception e) {
                log.error("RAG retrieval failed", e);
            }
        }

        if ((request.getLawSources() != null && !request.getLawSources().isEmpty()) ||
            (request.getCaseSources() != null && !request.getCaseSources().isEmpty())) {
            userPromptBuilder.append("\n\n=== 法律资料库 ===");

            if (request.getLawSources() != null && !request.getLawSources().isEmpty()) {
                userPromptBuilder.append("\n【相关法规】：\n");
                for (int i = 0; i < request.getLawSources().size(); i++) {
                    Map<String, String> law = request.getLawSources().get(i);
                    userPromptBuilder.append(i + 1).append(". ").append(law.get("name"));
                    if (law.get("content") != null) {
                        userPromptBuilder.append("：").append(law.get("content"));
                    }
                    userPromptBuilder.append("\n");
                }
            }

            if (request.getCaseSources() != null && !request.getCaseSources().isEmpty()) {
                userPromptBuilder.append("\n【相关案例】：\n");
                for (int i = 0; i < request.getCaseSources().size(); i++) {
                    Map<String, String> caseInfo = request.getCaseSources().get(i);
                    userPromptBuilder.append(i + 1).append(". ").append(caseInfo.get("title"));
                    if (caseInfo.get("content") != null) {
                        userPromptBuilder.append("：").append(caseInfo.get("content"));
                    }
                    userPromptBuilder.append("\n");
                }
            }

            userPromptBuilder.append("\n请根据以上参考资料回答用户问题，并务必在回答中标注每条法律条文或案例的来源。");
        }

        if (!aiEnabled || apiKey == null || apiKey.isEmpty()) {
            return getMockResponse(request.getMessage(), request.getLawSources(), request.getCaseSources());
        }

        try {
            String systemPrompt = "你是一位资深执业律师，擅长民法、劳动法、合同法等领域。\n" +
                    "你以专业的法律视角为用户提供帮助。\n\n" +
                    "【回答规范】\n" +
                    "1. 语言专业、严谨，条理清晰\n" +
                    "2. 优先引用现行有效法律法规条文，标注条款编号\n" +
                    "3. 参考案例需说明案件基本信息\n" +
                    "4. 复杂问题提供多角度分析\n" +
                    "5. 主动提示相关风险和注意事项\n\n" +
                    "【格式要求】\n" +
                    "- 使用 ## 标注二级标题\n" +
                    "- 使用 ### 标注三级标题\n" +
                    "- 使用 - 列举要点\n" +
                    "- 法条引用格式：【《法名》第X条】\n\n" +
                    "【免责声明】\n" +
                    "- 明确说明本回答仅供参考，不构成正式法律意见\n" +
                    "-涉及重大权益的问题，建议咨询执业律师\n\n" +
                    "【文书生成引导】\n" +
                    "- 如用户需要起草文书，主动询问关键要素\n" +
                    "- 根据用户需求推荐合适的文书类型\n\n" +
                    "请根据以上规范回答用户问题：";

            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPromptBuilder.toString())
                ),
                "max_tokens", maxTokens,
                "temperature", temperature,
                "extra_body", Map.of("reasoning_split", true)
            );

            ObjectMapper mapper = new ObjectMapper();
            String jsonRequest = mapper.writeValueAsString(requestBody);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                log.error("AI API error: {} - {}", response.statusCode(), response.body());
                return getMockResponse(request.getMessage(), request.getLawSources(), request.getCaseSources());
            }
        } catch (Exception e) {
            log.error("AI chat error", e);
            return getMockResponse(request.getMessage(), request.getLawSources(), request.getCaseSources());
        }
    }

    private ChatResponse parseResponse(String responseBody) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            String reasoning = "";
            if (root.path("choices").get(0).path("message").has("reasoning_details")) {
                reasoning = root.path("choices").get(0).path("message")
                        .path("reasoning_details").get(0).path("text").asText();
            }
            long tokens = root.path("usage").path("total_tokens").asLong(0);

            return ChatResponse.builder()
                    .id(root.path("id").asText())
                    .content(content)
                    .reasoning(reasoning)
                    .model(root.path("model").asText())
                    .tokens(tokens)
                    .build();
        } catch (Exception e) {
            log.error("Parse AI response error", e);
            return getMockResponse("", null, null);
        }
    }

    private ChatResponse getMockResponse(String question, List<Map<String, String>> lawSources, List<Map<String, String>> caseSources) {
        StringBuilder response = new StringBuilder();
        response.append("您的问题是：「").append(question).append("」\n\n");

        if ((lawSources != null && !lawSources.isEmpty()) || (caseSources != null && !caseSources.isEmpty())) {
            response.append("根据检索到的资料，我为您解答如下：\n\n");

            if (lawSources != null && !lawSources.isEmpty()) {
                response.append("## 相关法律法规\n\n");
                for (Map<String, String> law : lawSources) {
                    response.append("- ").append(law.get("name"));
                    if (law.get("content") != null) {
                        response.append("：").append(law.get("content"));
                    }
                    response.append(" 【来源】\n");
                }
                response.append("\n");
            }

            if (caseSources != null && !caseSources.isEmpty()) {
                response.append("## 相关案例参考\n\n");
                for (Map<String, String> caseInfo : caseSources) {
                    response.append("- ").append(caseInfo.get("title"));
                    if (caseInfo.get("content") != null) {
                        response.append("：").append(caseInfo.get("content"));
                    }
                    response.append(" 【来源】\n");
                }
            }
        } else {
            response.append("抱歉，目前没有检索到相关的法律资料来回答您的问题。\n\n");
            response.append("建议您：\n");
            response.append("1. 尝试使用更具体的关键词搜索\n");
            response.append("2. 换个角度描述您的问题\n");
            response.append("3. 如需专业法律帮助，请咨询律师\n");
        }

        return ChatResponse.builder()
                .id("mock-" + System.currentTimeMillis())
                .content(response.toString())
                .model("mock")
                .build();
    }
}
