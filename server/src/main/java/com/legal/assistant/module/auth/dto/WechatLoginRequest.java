package com.legal.assistant.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WechatLoginRequest {

    @NotBlank(message = "微信 code 不能为空")
    private String code;

    private String encryptedData;

    private String iv;

    private String rawData;

    private String signature;

    private String userinfo;
}
