package com.legal.assistant.module.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDocumentRequest {
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "文书类型不能为空")
    private String docType;

    private String content;
    private String caseId;
    private String templateId;
}