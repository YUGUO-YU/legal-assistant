package com.legal.assistant.module.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.assistant.module.ai.dto.ChatRequest;
import com.legal.assistant.module.ai.dto.ChatResponse;
import com.legal.assistant.module.ai.service.AiService;
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

    private final HttpClient httpClient;
    private final WebSearchService searchService;

    public MiniMaxAiServiceImpl(WebSearchService searchService) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.searchService = searchService;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        StringBuilder userPromptBuilder = new StringBuilder();
        userPromptBuilder.append(request.getMessage());

        boolean hasWebSearch = false;
        StringBuilder webSearchResults = new StringBuilder();

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

        if (hasWebSearch) {
            userPromptBuilder.append(webSearchResults.toString());
            userPromptBuilder.append("\n\n请根据以上网络搜索结果，用中文详细回答用户问题。回答要专业、实用，优先引用搜索到的来源。");
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
            String systemPrompt = "你是一个专业的法律助手，名为法律小精灵。你的职责是：\n" +
                    "1. 回答用户关于法律问题的咨询\n" +
                    "2. 提供法律知识科普\n" +
                    "3. 协助分析案情和提供建议\n" +
                    "4. 帮助起草简单的法律文书\n" +
                    "5. 语言要专业但易懂，温暖且有帮助\n\n" +
                    "请注意：\n" +
                    "- 必须根据提供的参考资料回答，不要编造法律条文\n" +
                    "- 每个法律条文都要标注来源，如：【来源：《中华人民共和国合同法》第X条】\n" +
                    "- 每个案例都要标注来源，如：【来源：XXX案】\n" +
                    "- 如果资料不足，明确说明哪些内容是依据不足需要核实\n" +
                    "- 如果涉及复杂案件，引导用户寻求专业律师帮助";

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
