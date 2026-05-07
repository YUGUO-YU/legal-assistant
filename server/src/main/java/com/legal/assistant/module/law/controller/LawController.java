package com.legal.assistant.module.law.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.law.dto.*;
import com.legal.assistant.module.law.service.LawService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/laws")
@RequiredArgsConstructor
public class LawController {

    private final LawService lawService;

    @GetMapping("/search")
    public Result<LawSearchResponse> search(LawSearchRequest request) {
        return Result.success(lawService.search(request));
    }

    @GetMapping("/{id}")
    public Result<LawDetailResponse> getDetail(@PathVariable String id) {
        return Result.success(lawService.getDetail(id));
    }

    @GetMapping("/{id}/related")
    public Result<LawSearchResponse> getRelated(@PathVariable String id) {
        return Result.success(lawService.getRelated(id));
    }
}