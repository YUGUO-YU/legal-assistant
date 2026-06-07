package com.legal.assistant.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WechatMiniLoginRequest {
    
    @NotBlank(message = "code 不能为空")
    private String code;
    
    private String encryptedData;
    
    private String iv;
}
