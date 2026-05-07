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
public class CompanyDetailResponse {
    private String id;
    private String name;
    private String creditCode;
    private String legalPerson;
    private String capital;
    private String establishDate;
    private String status;
    private String businessScope;
    private List<Shareholder> shareholders;
    private List<RiskInfo> risks;
    private List< lawsuit> lawsuits;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Shareholder {
        private String name;
        private Double sharePercent;
        private String capital;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskInfo {
        private String type;
        private String description;
        private String date;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class lawsuit {
        private String caseNumber;
        private String title;
        private String date;
    }
}