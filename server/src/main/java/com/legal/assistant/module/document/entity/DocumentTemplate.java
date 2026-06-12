package com.legal.assistant.module.document.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文书模板实体
 */
@Data
@TableName("document_templates")
public class DocumentTemplate {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String category;
    
    private String description;
    
    private String filePath;
    
    @TableField("variables")
    private String variablesJson;
    
    private Integer downloadCount;
    
    private Boolean isPublic;
    
    @TableLogic
    private Boolean deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    public List<TemplateVariable> getVariables() {
        if (variablesJson == null || variablesJson.isEmpty()) {
            return null;
        }
        try {
            return JSON.parseArray(variablesJson, TemplateVariable.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    public void setVariables(List<TemplateVariable> variables) {
        if (variables == null) {
            this.variablesJson = null;
        } else {
            this.variablesJson = JSON.toJSONString(variables);
        }
    }
}
