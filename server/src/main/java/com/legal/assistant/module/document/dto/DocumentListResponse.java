package com.legal.assistant.module.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentListResponse {
    private List<DocumentResponse> list;
    private Long total;
    private Integer page;
    private Integer pageSize;
}