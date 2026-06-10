package com.legal.assistant.module.legal.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.legal.dto.LegalSearchRequest;
import com.legal.assistant.module.legal.dto.LegalSearchResponse;
import com.legal.assistant.module.legal.service.LegalDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/legal")
@RequiredArgsConstructor
public class LegalDataController {

    private final LegalDataService legalDataService;

    @GetMapping("/search")
    public Result<LegalSearchResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false) String court,
            @RequestParam(required = false) String dateRange,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {

        LegalSearchRequest request = LegalSearchRequest.builder()
                .keyword(keyword)
                .type(type)
                .court(court)
                .dateRange(dateRange)
                .page(page)
                .pageSize(pageSize)
                .build();

        if ("all".equals(type)) {
            return Result.success(legalDataService.searchAll(request));
        } else if ("case".equals(type)) {
            return Result.success(legalDataService.searchCases(request));
        } else if ("law".equals(type)) {
            return Result.success(legalDataService.searchLaws(request));
        } else if ("company".equals(type)) {
            return Result.success(legalDataService.searchCompanies(request));
        }

        return Result.success(legalDataService.searchAll(request));
    }

    @GetMapping("/cases/search")
    public Result<LegalSearchResponse> searchCases(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String court,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {

        LegalSearchRequest request = LegalSearchRequest.builder()
                .keyword(keyword)
                .court(court)
                .page(page)
                .pageSize(pageSize)
                .build();

        return Result.success(legalDataService.searchCases(request));
    }

    @GetMapping("/laws/search")
    public Result<LegalSearchResponse> searchLaws(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {

        LegalSearchRequest request = LegalSearchRequest.builder()
                .keyword(keyword)
                .page(page)
                .pageSize(pageSize)
                .build();

        return Result.success(legalDataService.searchLaws(request));
    }

    @GetMapping("/companies/search")
    public Result<LegalSearchResponse> searchCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {

        LegalSearchRequest request = LegalSearchRequest.builder()
                .keyword(keyword)
                .page(page)
                .pageSize(pageSize)
                .build();

        return Result.success(legalDataService.searchCompanies(request));
    }
}
