package com.legal.assistant.module.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String phone;
    private String email;
    private String passwordHash;
    private String nickname;
    private String avatarUrl;

    @TableField("wechat_openid")
    private String wechatOpenid;

    @TableField("wechat_unionid")
    private String wechatUnionid;

    @TableField("role")
    private String role;

    private Integer status;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
