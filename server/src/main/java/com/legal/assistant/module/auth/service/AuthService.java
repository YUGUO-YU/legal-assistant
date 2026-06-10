package com.legal.assistant.module.auth.service;

import com.legal.assistant.module.auth.dto.*;
import com.legal.assistant.module.auth.entity.User;

public interface AuthService {
    LoginResponse phoneLogin(PhoneLoginRequest request);
    LoginResponse emailCodeLogin(EmailCodeLoginRequest request);
    LoginResponse register(EmailRegisterRequest request);
    void sendSmsCode(SendSmsRequest request);
    void sendEmailCode(SendSmsRequest request);
    LoginResponse refreshToken(String refreshToken);
    void logout(String userId);
    
    User findOrCreateWechatUser(String openid, String unionid, WechatLoginRequest request);
    void updateUserPhone(String userId, String phone);
    LoginResponse generateToken(User user);
}
