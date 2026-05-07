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
public class LawSearchResponse {
    private List<LawItem> list;
    private Long total;
    private Integer page;
    private Integer pageSize;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LawItem {
        private String id;
        private String title;
        private String level;
        private String organ;
        private String issueDate;
        private String effectiveDate;
        private String status;
    }
}