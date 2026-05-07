package com.legal.assistant.module.case.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseSearchRequest {
    private String keyword;
    private String caseType;
    private String courtLevel;
    private String region;
    private String dateRange;
    private Integer page;
    private Integer pageSize;
}