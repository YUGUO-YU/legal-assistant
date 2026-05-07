package com.legal.assistant.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(0, "success"),

    SYSTEM_ERROR(1000, "系统内部错误"),

    AUTH_ERROR(2000, "认证错误"),
    AUTH_TOKEN_EXPIRED(2001, "Token 已过期"),
    AUTH_TOKEN_INVALID(2002, "Token 无效"),
    AUTH_CREDENTIALS_ERROR(2003, "用户名或密码错误"),
    AUTH_SMS_CODE_ERROR(2004, "验证码错误"),
    AUTH_SMS_CODE_EXPIRED(2005, "验证码已过期"),

    PERMISSION_DENIED(3000, "权限不足"),

    PARAM_ERROR(4000, "参数错误"),
    PARAM_MISSING(4001, "缺少必要参数"),
    PARAM_INVALID(4002, "参数格式不正确"),

    BUSINESS_ERROR(5000, "业务逻辑错误"),
    USER_NOT_FOUND(5001, "用户不存在"),
    USER_ALREADY_EXISTS(5002, "用户已存在"),
    DOCUMENT_NOT_FOUND(5001, "文书不存在"),
    CASE_NOT_FOUND(5002, "案例不存在"),
    LAW_NOT_FOUND(5003, "法规不存在"),
    COMPANY_NOT_FOUND(5004, "企业不存在"),
    LEAD_NOT_FOUND(5005, "案源不存在"),

    RESOURCE_NOT_FOUND(6000, "资源不存在");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}