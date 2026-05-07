package com.legal.assistant.module.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchRequest {
    private String keyword;
    private String creditCode;
    private Integer page;
    private Integer pageSize;
}