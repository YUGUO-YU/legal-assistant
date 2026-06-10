package com.legal.assistant.module.ai.service;

import com.legal.assistant.module.ai.dto.ChatRequest;
import com.legal.assistant.module.ai.dto.ChatResponse;

public interface AiService {
    ChatResponse chat(ChatRequest request);
}
