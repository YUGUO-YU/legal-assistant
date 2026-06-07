package com.legal.assistant.module.auth.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.module.auth.dto.WechatLoginRequest;
import com.legal.assistant.module.auth.dto.WechatMiniLoginRequest;
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
import java.util.Base64;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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

    /**
     * 小程序一键登录（获取手机号）
     */
    public LoginResponse miniLogin(WechatMiniLoginRequest request) {
        try {
            // 1. 通过 code 获取 session_key
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("appid", appid);
            uriVariables.put("secret", secret);
            uriVariables.put("js_code", request.getCode());

            String response = restTemplate.getForObject(JS_CODE_SESSION_URL, String.class, uriVariables);
            log.info("微信小程序登录响应：{}", response);

            JSONObject result = JSON.parseObject(response);
            if (result.containsKey("errcode")) {
                throw new BusinessException(1000, "微信登录失败：" + result.getString("errmsg"));
            }

            String sessionKey = result.getString("session_key");
            String openid = result.getString("openid");

            if (sessionKey == null || openid == null) {
                throw new BusinessException(1000, "获取微信 session 失败");
            }

            // 2. 解密手机号
            String phoneNumber = null;
            if (request.getEncryptedData() != null && request.getIv() != null) {
                phoneNumber = decryptPhoneNumber(
                    request.getEncryptedData(),
                    sessionKey,
                    request.getIv()
                );
            }

            // 3. 创建或获取用户
            User user = authService.findOrCreateWechatUser(openid, null, null);
            
            // 如果获取到手机号，更新用户信息
            if (phoneNumber != null && user.getPhone() == null) {
                authService.updateUserPhone(user.getId(), phoneNumber);
            }

            return authService.generateToken(user);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信小程序登录异常", e);
            throw new BusinessException(1000, "微信登录失败，请稍后重试");
        }
    }

    /**
     * 解密微信手机号
     */
    private String decryptPhoneNumber(String encryptedData, String sessionKey, String iv) {
        try {
            byte[] dataByte = Base64.getDecoder().decode(encryptedData);
            byte[] keyByte = Base64.getDecoder().decode(sessionKey);
            byte[] ivByte = Base64.getDecoder().decode(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivByte);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(dataByte);
            String decryptedData = new String(decrypted);

            JSONObject jsonObject = JSON.parseObject(decryptedData);
            return jsonObject.getString("phoneNumber");

        } catch (Exception e) {
            log.error("解密手机号失败", e);
            throw new BusinessException(1000, "解密手机号失败");
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
