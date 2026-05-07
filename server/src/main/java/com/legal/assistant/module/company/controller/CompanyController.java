package com.legal.assistant.module.company.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.company.dto.*;
import com.legal.assistant.module.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/search")
    public Result<CompanySearchResponse> search(CompanySearchRequest request) {
        return Result.success(companyService.search(request));
    }

    @GetMapping("/{id}")
    public Result<CompanyDetailResponse> getDetail(@PathVariable String id) {
        return Result.success(companyService.getDetail(id));
    }

    @GetMapping("/{id}/shareholders")
    public Result<CompanyDetailResponse> getShareholders(@PathVariable String id) {
        return Result.success(companyService.getShareholders(id));
    }

    @GetMapping("/{id}/risk")
    public Result<CompanyDetailResponse> getRiskInfo(@PathVariable String id) {
        return Result.success(companyService.getRiskInfo(id));
    }

    @GetMapping("/{id}/graph")
    public Result<Object> getGraph(@PathVariable String id) {
        return Result.success(companyService.getGraph(id));
    }
}