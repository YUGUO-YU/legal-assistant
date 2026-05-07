package com.legal.assistant.module.document.service;

import com.legal.assistant.module.document.dto.*;

public interface DocumentService {
    DocumentListResponse getList(String userId, String type, String status, String keyword, Integer page, Integer pageSize);
    DocumentResponse getById(String userId, String id);
    DocumentResponse create(String userId, CreateDocumentRequest request);
    DocumentResponse update(String userId, String id, UpdateDocumentRequest request);
    void delete(String userId, String id);
    List<DocumentResponse> getVersions(String userId, String id);
    String exportDocument(String userId, String id, String format);
}