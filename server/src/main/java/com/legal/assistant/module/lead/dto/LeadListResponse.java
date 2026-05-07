package com.legal.assistant.module.lead.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadListResponse {
    private List<LeadResponse> list;
    private Long total;
    private Integer page;
    private Integer pageSize;
}