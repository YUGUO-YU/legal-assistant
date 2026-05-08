package com.legal.assistant.module.cases.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookmarkCaseRequest {
    @NotBlank(message = "外部案例ID不能为空")
    private String externalId;

    @NotBlank(message = "来源不能为空")
    private String source;

    private String title;
    private String note;
}