package com.legal.assistant.module.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.legal.assistant.common.typehandler.FastjsonTypeHandler;
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
    
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<TemplateVariable> variables;
    
    private Integer downloadCount;
    
    private Boolean isPublic;
    
    @TableLogic
    private Boolean deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
