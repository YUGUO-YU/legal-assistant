package com.legal.assistant.module.ai.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String context;
    private String conversationId;
}
