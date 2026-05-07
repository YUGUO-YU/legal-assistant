package com.legal.assistant.module.document.dto;

import com.legal.assistant.module.document.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String id;
    private String title;
    private String docType;
    private String content;
    private String status;
    private String caseId;
    private Integer version;
    private String createdAt;
    private String updatedAt;

    public static DocumentResponse fromEntity(Document doc) {
        return DocumentResponse.builder()
            .id(doc.getId())
            .title(doc.getTitle())
            .docType(doc.getDocType())
            .content(doc.getContent())
            .status(doc.getStatus())
            .caseId(doc.getCaseId())
            .version(doc.getVersion())
            .createdAt(doc.getCreatedAt() != null ? doc.getCreatedAt().toString() : null)
            .updatedAt(doc.getUpdatedAt() != null ? doc.getUpdatedAt().toString() : null)
            .build();
    }
}