package com.legal.assistant.module.lead.service;

import com.legal.assistant.module.lead.dto.*;

public interface LeadService {
    LeadListResponse getList(String userId, String status, Integer page, Integer pageSize);
    LeadResponse getById(String userId, String id);
    LeadResponse create(String userId, CreateLeadRequest request);
    LeadResponse update(String userId, String id, UpdateLeadRequest request);
    void updateStatus(String userId, String id, String status);
    void delete(String userId, String id);
}