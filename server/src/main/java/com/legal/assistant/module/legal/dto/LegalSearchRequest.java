package com.legal.assistant.module.legal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalSearchRequest {
    private String keyword;
    private String type;
    private String court;
    private String dateRange;
    private Integer page;
    private Integer pageSize;
}
