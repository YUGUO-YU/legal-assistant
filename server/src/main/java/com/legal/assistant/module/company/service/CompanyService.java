package com.legal.assistant.module.company.service;

import com.legal.assistant.module.company.dto.*;

public interface CompanyService {
    CompanySearchResponse search(CompanySearchRequest request);
    CompanyDetailResponse getDetail(String id);
    CompanyDetailResponse getShareholders(String id);
    CompanyDetailResponse getRiskInfo(String id);
    Object getGraph(String id);
}