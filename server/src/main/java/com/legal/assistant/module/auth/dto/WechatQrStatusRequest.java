package com.legal.assistant.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信扫码登录状态查询请求
 */
@Data
public class WechatQrStatusRequest {
    @NotBlank(message = "scene 不能为空")
    private String scene;
}
