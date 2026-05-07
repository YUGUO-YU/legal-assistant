package com.legal.assistant.module.lead.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateLeadRequest {
    private String title;
    private String description;
    private String source;
    private List<String> tags;
    private String status;
}