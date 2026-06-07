package com.legal.assistant.module.auth.dto;

import lombok.Data;

/**
 * 微信二维码登录响应（前端展示二维码）
 */
@Data
public class WechatQrCodeResponse {
    /**
     * 二维码链接（用于生成二维码）
     */
    private String qrCodeUrl;
    
    /**
     * 场景值（用于轮询查询登录状态）
     */
    private String scene;
    
    /**
     * 二维码过期时间（秒）
     */
    private Long expiresIn;
}
