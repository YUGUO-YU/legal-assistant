package com.legal.assistant.module.auth.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 微信扫码登录状态响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatQrStatusResponse {
    /**
     * 状态：waiting(等待扫码), scanned(已扫码未确认), confirmed(已确认登录), expired(二维码过期)
     */
    private String status;
    
    /**
     * 用户信息（仅在 confirmed 状态时有值）
     */
    private LoginResponse user;
    
    /**
     * 提示信息
     */
    private String message;
}
