package com.legal.assistant.module.cases.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDetailResponse {
    private String id;
    private String caseNumber;
    private String title;
    private String court;
    private String caseType;
    private String procedure;
    private String judgmentDate;
    private String plaintiff;
    private String defendant;
    private String judge;
    private String lawyer;
    private String content;
    private List<String> relatedLaws;
    private List<CaseSearchResponse.CaseItem> similarCases;
    private Boolean bookmarked;
}