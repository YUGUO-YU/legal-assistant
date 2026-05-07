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
public class CompanySearchResponse {
    private List<CompanyItem> list;
    private Long total;
    private Integer page;
    private Integer pageSize;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyItem {
        private String id;
        private String name;
        private String creditCode;
        private String legalPerson;
        private String capital;
        private String establishDate;
        private String status;
    }
}