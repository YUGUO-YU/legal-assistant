package com.legal.assistant.module.ai.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ChatRequest {
    private String message;
    private String context;
    private String conversationId;
    private List<Map<String, String>> lawSources;
    private List<Map<String, String>> caseSources;
}
