package com.legal.assistant.module.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.assistant.module.ai.dto.ChatRequest;
import com.legal.assistant.module.ai.dto.ChatResponse;
import com.legal.assistant.module.ai.service.AiService;
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

    public MiniMaxAiServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        if (!aiEnabled || apiKey == null || apiKey.isEmpty()) {
            return getMockResponse(request.getMessage());
        }

        try {
            String systemPrompt = "你是一个专业的法律助手，名为法律小精灵。你的职责是：\n" +
                    "1. 回答用户关于法律问题的咨询\n" +
                    "2. 提供法律知识科普\n" +
                    "3. 协助分析案情和提供建议\n" +
                    "4. 帮助起草简单的法律文书\n" +
                    "5. 语言要专业但易懂，温暖且有帮助\n\n" +
                    "请注意：\n" +
                    "- 不要提供具体的法律建议说'建议您咨询律师'\n" +
                    "- 尽量给出实用的法律知识和方法\n" +
                    "- 如果涉及复杂案件，引导用户寻求专业律师帮助";

            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", request.getMessage())
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
                return getMockResponse(request.getMessage());
            }
        } catch (Exception e) {
            log.error("AI chat error", e);
            return getMockResponse(request.getMessage());
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
            return getMockResponse("");
        }
    }

    private ChatResponse getMockResponse(String question) {
        String response = "您的问题是：「" + question + "」\n\n" +
                "根据我的分析，这是一个涉及民事法律关系的问题。\n\n" +
                "建议您：\n" +
                "1. 收集相关证据材料\n" +
                "2. 明确诉讼请求\n" +
                "3. 了解相关法律规定\n" +
                "4. 必要时咨询专业律师\n\n" +
                "您还想了解更多信息吗？";

        return ChatResponse.builder()
                .id("mock-" + System.currentTimeMillis())
                .content(response)
                .model("mock")
                .build();
    }
}
