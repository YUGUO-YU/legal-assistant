package com.legal.assistant.module.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("document")
public class Document {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;
    private String caseId;
    private String title;

    @TableField("doc_type")
    private String docType;

    private String content;
    private String status;
    private Integer version;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}