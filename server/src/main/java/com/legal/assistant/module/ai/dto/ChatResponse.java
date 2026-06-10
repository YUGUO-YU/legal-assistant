package com.legal.assistant.module.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String id;
    private String content;
    private String reasoning;
    private String model;
    private Long tokens;
}
