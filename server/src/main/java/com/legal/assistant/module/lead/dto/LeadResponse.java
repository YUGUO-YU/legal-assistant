package com.legal.assistant.module.lead.dto;

import com.legal.assistant.module.lead.entity.Lead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponse {
    private String id;
    private String title;
    private String description;
    private String source;
    private List<String> tags;
    private String status;
    private String createdAt;
    private String updatedAt;

    public static LeadResponse fromEntity(Lead lead) {
        return LeadResponse.builder()
            .id(lead.getId())
            .title(lead.getTitle())
            .description(lead.getDescription())
            .source(lead.getSource())
            .tags(lead.getTags() != null ? List.of(lead.getTags().split(",")) : null)
            .status(lead.getStatus())
            .createdAt(lead.getCreatedAt() != null ? lead.getCreatedAt().toString() : null)
            .updatedAt(lead.getUpdatedAt() != null ? lead.getUpdatedAt().toString() : null)
            .build();
    }
}