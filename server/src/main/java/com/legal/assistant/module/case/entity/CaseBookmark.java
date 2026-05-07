package com.legal.assistant.module.case.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("case_bookmark")
public class CaseBookmark {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;
    private String externalId;
    private String source;
    private String title;
    private String note;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}