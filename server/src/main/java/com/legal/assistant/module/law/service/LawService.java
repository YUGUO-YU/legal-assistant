package com.legal.assistant.module.law.service;

import com.legal.assistant.module.law.dto.*;

public interface LawService {
    LawSearchResponse search(LawSearchRequest request);
    LawDetailResponse getDetail(String id);
    LawSearchResponse getRelated(String id);
}