package com.legal.assistant.module.lead.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.lead.dto.*;
import com.legal.assistant.module.lead.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    public Result<LeadListResponse> getList(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(leadService.getList(userId, status, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<LeadResponse> getById(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        return Result.success(leadService.getById(userId, id));
    }

    @PostMapping
    public Result<LeadResponse> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateLeadRequest request) {
        return Result.success(leadService.create(userId, request));
    }

    @PutMapping("/{id}")
    public Result<LeadResponse> update(
            @AuthenticationPrincipal String userId,
            @PathVariable String id,
            @RequestBody UpdateLeadRequest request) {
        return Result.success(leadService.update(userId, id, request));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @AuthenticationPrincipal String userId,
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        leadService.updateStatus(userId, id, body.get("status"));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        leadService.delete(userId, id);
        return Result.success();
    }
}