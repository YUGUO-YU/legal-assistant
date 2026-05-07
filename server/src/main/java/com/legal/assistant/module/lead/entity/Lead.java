package com.legal.assistant.module.lead.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("lead")
public class Lead {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;
    private String title;
    private String description;
    private String source;
    private String tags;
    private String status;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}