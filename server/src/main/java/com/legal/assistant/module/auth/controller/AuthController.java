package com.legal.assistant.module.auth.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.auth.dto.*;
import com.legal.assistant.module.auth.service.WechatService;
import com.legal.assistant.module.auth.service.WechatOpenService;
import com.legal.assistant.module.auth.service.AuthService;
import com.legal.assistant.module.auth.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final WechatService wechatService;
    private final WechatOpenService wechatOpenService;

    @PostMapping("/phone/login")
    public Result<LoginResponse> phoneLogin(@Valid @RequestBody PhoneLoginRequest request) {
        return Result.success(authService.phoneLogin(request));
    }

    @PostMapping("/wechat/login")
    public Result<LoginResponse> wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        return Result.success(wechatService.login(request));
    }

    /**
     * 微信小程序一键登录（获取手机号）
     */
    @PostMapping("/wechat/mini/login")
    public Result<LoginResponse> wechatMiniLogin(@Valid @RequestBody WechatMiniLoginRequest request) {
        return Result.success(wechatService.miniLogin(request));
    }

    /**
     * H5 微信扫码登录 - 生成二维码
     */
    @GetMapping("/wechat/qr/generate")
    public Result<WechatQrCodeResponse> generateWechatQrCode() {
        return Result.success(wechatOpenService.generateQrCode());
    }
    
    /**
     * H5 微信扫码登录 - 查询状态
     */
    @PostMapping("/wechat/qr/status")
    public Result<WechatQrStatusResponse> checkWechatQrStatus(@Valid @RequestBody WechatQrStatusRequest request) {
        return Result.success(wechatOpenService.checkQrStatus(request.getScene()));
    }
    
    /**
     * H5 微信扫码登录 - 微信回调
     */
    @GetMapping("/wechat/callback")
    public String wechatCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {
        try {
            WechatQrStatusResponse response = wechatOpenService.handleCallback(code, state);
            if ("confirmed".equals(response.getStatus())) {
                return "redirect:/?login=success";
            } else {
                return "redirect:/?login=failed&msg=" + response.getMessage();
            }
        } catch (Exception e) {
            return "redirect:/?login=failed&msg=" + e.getMessage();
        }
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
    
    /**
     * 发送邮件验证码
     */
    @PostMapping("/email/code/send")
    public Result<Void> sendEmailCode(@Valid @RequestBody SendEmailCodeRequest request) {
        ((AuthServiceImpl)authService).sendEmailCode(request.getEmail());
        return Result.success();
    }
    
    /**
     * 邮箱验证码登录
     */
    @PostMapping("/email/code/login")
    public Result<LoginResponse> emailCodeLogin(@Valid @RequestBody EmailCodeLoginRequest request) {
        return Result.success(authService.emailCodeLogin(request));
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
