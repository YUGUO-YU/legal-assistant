package com.legal.assistant.module.auth.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.module.auth.dto.WechatLoginRequest;
import com.legal.assistant.module.auth.dto.LoginResponse;
import com.legal.assistant.module.auth.entity.User;
import com.legal.assistant.module.auth.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WechatService {

    private final RestTemplate restTemplate;
    private final AuthServiceImpl authService;

    @Value("${wechat.miniapp.appid:}")
    private String appid;

    @Value("${wechat.miniapp.secret:}")
    private String secret;

    private static final String JS_CODE_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type=authorization_code";

    public LoginResponse login(WechatLoginRequest request) {
        try {
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("appid", appid);
            uriVariables.put("secret", secret);
            uriVariables.put("js_code", request.getCode());

            String response = restTemplate.getForObject(JS_CODE_SESSION_URL, String.class, uriVariables);
            log.info("微信登录响应：{}", response);

            JSONObject result = JSON.parseObject(response);
            if (result.containsKey("errcode")) {
                throw new BusinessException(1000, "微信登录失败：" + result.getString("errmsg"));
            }

            String openid = result.getString("openid");
            String sessionKey = result.getString("session_key");
            String unionid = result.getString("unionid");

            if (openid == null) {
                throw new BusinessException(1000, "获取微信用户信息失败");
            }

            User user = authService.findOrCreateWechatUser(openid, unionid, request);
            
            return authService.generateToken(user);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录异常", e);
            throw new BusinessException(1000, "微信登录失败，请稍后重试");
        }
    }

    public String getAccessToken() {
        try {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={secret}";
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("appid", appid);
            uriVariables.put("secret", secret);

            String response = restTemplate.getForObject(url, String.class, uriVariables);
            JSONObject result = JSON.parseObject(response);
            
            if (result.containsKey("errcode")) {
                throw new BusinessException(1000, "获取 access_token 失败：" + result.getString("errmsg"));
            }

            return result.getString("access_token");
        } catch (Exception e) {
            log.error("获取 access_token 异常", e);
            throw new BusinessException(1000, "获取微信 access_token 失败");
        }
    }
}
