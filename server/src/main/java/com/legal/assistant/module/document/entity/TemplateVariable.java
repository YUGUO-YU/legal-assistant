package com.legal.assistant.module.document.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板变量
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateVariable {
    
    private String name;
    
    private String label;
    
    private String type;
    
    private Boolean required;
    
    private String placeholder;
    
    private Object options;
}
