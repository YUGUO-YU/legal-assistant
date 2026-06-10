package com.legal.assistant.module.legal.service;

import com.legal.assistant.module.legal.dto.LegalSearchRequest;
import com.legal.assistant.module.legal.dto.LegalSearchResponse;

public interface LegalDataService {
    LegalSearchResponse searchCases(LegalSearchRequest request);
    LegalSearchResponse searchLaws(LegalSearchRequest request);
    LegalSearchResponse searchCompanies(LegalSearchRequest request);
    LegalSearchResponse searchAll(LegalSearchRequest request);
}
