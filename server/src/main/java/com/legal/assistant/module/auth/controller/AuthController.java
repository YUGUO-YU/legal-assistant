package com.legal.assistant.module.auth.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.auth.dto.*;
import com.legal.assistant.module.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/phone/login")
    public Result<LoginResponse> phoneLogin(@Valid @RequestBody PhoneLoginRequest request) {
        return Result.success(authService.phoneLogin(request));
    }

    @PostMapping("/email/login")
    public Result<LoginResponse> emailLogin(@Valid @RequestBody EmailLoginRequest request) {
        return Result.success(authService.emailLogin(request));
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody EmailRegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/sms/send")
    public Result<Void> sendSmsCode(@Valid @RequestBody SendSmsRequest request) {
        authService.sendSmsCode(request);
        return Result.success();
    }

    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestBody @Valid java.util.Map<String, String> request) {
        return Result.success(authService.refreshToken(request.get("refreshToken")));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return Result.success();
    }
}