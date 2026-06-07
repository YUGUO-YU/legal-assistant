package com.legal.assistant.module.auth.service;

import com.legal.assistant.module.auth.dto.*;

/**
 * 微信开放平台服务（网站应用扫码登录）
 */
public interface WechatOpenService {
    
    /**
     * 生成微信登录二维码
     * @return 二维码信息
     */
    WechatQrCodeResponse generateQrCode();
    
    /**
     * 查询二维码状态（轮询）
     * @param scene 场景值
     * @return 登录状态
     */
    WechatQrStatusResponse checkQrStatus(String scene);
    
    /**
     * 微信回调处理
     * @param code 授权码
     * @param state 状态参数（scene）
     * @return 登录结果
     */
    WechatQrStatusResponse handleCallback(String code, String state);
    
    /**
     * 微信登录（H5 跳转回调后的处理）
     * @param request 登录请求
     * @return 登录响应
     */
    com.legal.assistant.module.auth.dto.LoginResponse login(WechatLoginRequest request);
}
