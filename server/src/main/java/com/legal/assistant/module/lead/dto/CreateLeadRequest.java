package com.legal.assistant.module.lead.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class CreateLeadRequest {
    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;
    private String source;
    private List<String> tags;
}