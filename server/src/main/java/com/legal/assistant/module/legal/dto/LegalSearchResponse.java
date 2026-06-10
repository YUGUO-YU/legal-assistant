package com.legal.assistant.module.legal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalSearchResponse {
    private List<CaseItem> cases;
    private List<LawItem> laws;
    private List<CompanyItem> companies;
    private Long total;
    private Integer page;
    private Integer pageSize;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaseItem {
        private String id;
        private String caseNumber;
        private String title;
        private String court;
        private String caseType;
        private String procedure;
        private String judgmentDate;
        private String plaintiff;
        private String defendant;
        private String content;
        private String source;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LawItem {
        private String id;
        private String name;
        private String level;
        private String category;
        private String issueDate;
        private String effectiveDate;
        private String content;
        private String source;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyItem {
        private String id;
        private String name;
        private String creditCode;
        private String legalPerson;
        private String registeredCapital;
        private String status;
        private String establishDate;
        private String address;
    }
}
