package com.legal.assistant.module.auth.service;

import com.legal.assistant.module.auth.dto.*;

public interface AuthService {
    LoginResponse phoneLogin(PhoneLoginRequest request);
    LoginResponse emailLogin(EmailLoginRequest request);
    LoginResponse register(EmailRegisterRequest request);
    void sendSmsCode(SendSmsRequest request);
    LoginResponse refreshToken(String refreshToken);
    void logout(String userId);
}