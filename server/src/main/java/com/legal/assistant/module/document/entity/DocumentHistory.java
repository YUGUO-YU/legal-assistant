package com.legal.assistant.module.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.legal.assistant.common.typehandler.FastjsonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 文书生成历史实体
 */
@Data
@TableName("document_histories")
public class DocumentHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long templateId;
    
    private String fileName;
    
    private String filePath;
    
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> templateData;
    
    @TableLogic
    private Boolean deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
