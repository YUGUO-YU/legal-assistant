package com.legal.assistant.module.ai.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.ai.dto.ChatRequest;
import com.legal.assistant.module.ai.dto.ChatResponse;
import com.legal.assistant.module.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        return Result.success(aiService.chat(request));
    }
}
