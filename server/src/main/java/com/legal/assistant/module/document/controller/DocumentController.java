package com.legal.assistant.module.document.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.document.dto.*;
import com.legal.assistant.module.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public Result<DocumentListResponse> getList(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(documentService.getList(userId, type, status, keyword, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<DocumentResponse> getById(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        return Result.success(documentService.getById(userId, id));
    }

    @PostMapping
    public Result<DocumentResponse> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateDocumentRequest request) {
        return Result.success(documentService.create(userId, request));
    }

    @PutMapping("/{id}")
    public Result<DocumentResponse> update(
            @AuthenticationPrincipal String userId,
            @PathVariable String id,
            @RequestBody UpdateDocumentRequest request) {
        return Result.success(documentService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        documentService.delete(userId, id);
        return Result.success();
    }

    @GetMapping("/{id}/versions")
    public Result<List<DocumentResponse>> getVersions(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        return Result.success(documentService.getVersions(userId, id));
    }

    @PostMapping("/{id}/export")
    public Result<Map<String, String>> exportDocument(
            @AuthenticationPrincipal String userId,
            @PathVariable String id,
            @RequestParam(defaultValue = "pdf") String format) {
        String content = documentService.exportDocument(userId, id, format);
        return Result.success(Map.of("content", content, "format", format));
    }
}