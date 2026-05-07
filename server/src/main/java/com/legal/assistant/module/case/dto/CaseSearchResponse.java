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
public class CaseSearchResponse {
    private List<CaseItem> list;
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
    }
}