package com.legal.assistant.module.law.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawSearchRequest {
    private String keyword;
    private String level;
    private String organ;
    private String dateRange;
    private Integer page;
    private Integer pageSize;
}