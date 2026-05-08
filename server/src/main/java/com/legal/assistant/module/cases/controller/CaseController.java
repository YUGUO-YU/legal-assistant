package com.legal.assistant.module.cases.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.cases.dto.*;
import com.legal.assistant.module.cases.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @GetMapping("/search")
    public Result<CaseSearchResponse> search(CaseSearchRequest request) {
        return Result.success(caseService.search(request));
    }

    @GetMapping("/{id}")
    public Result<CaseDetailResponse> getDetail(@PathVariable String id) {
        return Result.success(caseService.getDetail(id));
    }

    @GetMapping("/{id}/similar")
    public Result<CaseSearchResponse> getSimilarCases(@PathVariable String id) {
        return Result.success(caseService.getSimilarCases(id));
    }

    @PostMapping("/bookmark")
    public Result<Void> bookmark(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody BookmarkCaseRequest request) {
        caseService.bookmark(userId, request);
        return Result.success();
    }

    @GetMapping("/bookmarks")
    public Result<List<CaseSearchResponse.CaseItem>> getBookmarks(
            @AuthenticationPrincipal String userId) {
        return Result.success(caseService.getBookmarks(userId));
    }

    @DeleteMapping("/bookmark/{id}")
    public Result<Void> removeBookmark(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        caseService.removeBookmark(userId, id);
        return Result.success();
    }
}