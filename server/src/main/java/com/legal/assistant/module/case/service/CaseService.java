package com.legal.assistant.module.case.service;

import com.legal.assistant.module.case.dto.*;

import java.util.List;

public interface CaseService {
    CaseSearchResponse search(CaseSearchRequest request);
    CaseDetailResponse getDetail(String id);
    CaseSearchResponse getSimilarCases(String id);
    void bookmark(String userId, BookmarkCaseRequest request);
    List<CaseSearchResponse.CaseItem> getBookmarks(String userId);
    void removeBookmark(String userId, String id);
}