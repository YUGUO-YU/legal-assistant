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
public class LawDetailResponse {
    private String id;
    private String title;
    private String level;
    private String organ;
    private String issueDate;
    private String effectiveDate;
    private String status;
    private String content;
    private List<Chapter> chapters;
    private List<RelatedLaw> relatedLaws;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chapter {
        private String title;
        private String articles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedLaw {
        private String id;
        private String title;
    }
}