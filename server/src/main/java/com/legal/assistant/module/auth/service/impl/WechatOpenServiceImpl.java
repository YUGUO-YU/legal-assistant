package com.legal.assistant.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.common.result.ResultCode;
import com.legal.assistant.common.security.JwtTokenProvider;
import com.legal.assistant.module.auth.dto.*;
import com.legal.assistant.module.auth.entity.User;
import com.legal.assistant.module.auth.mapper.UserMapper;
import com.legal.assistant.module.auth.service.WechatOpenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class WechatOpenServiceImpl implements WechatOpenService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${wechat.open.appid:}")
    private String appid;
    
    @Value("${wechat.open.secret:}")
    private String secret;
    
    @Value("${wechat.open.redirect-uri:http://localhost:5173/wechat/callback}")
    private String redirectUri;

    private static final String QR_CODE_STATUS_PREFIX = "wechat:qr:status:";
    private static final String AUTH_CODE_PREFIX = "wechat:auth:code:";
    private static final Long QR_CODE_EXPIRE = 300L; // 5 分钟

    @Override
    public WechatQrCodeResponse generateQrCode() {
        String scene = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String redisKey = QR_CODE_STATUS_PREFIX + scene;
        redisTemplate.opsForValue().set(redisKey, "waiting", QR_CODE_EXPIRE, TimeUnit.SECONDS);
        
        String oauthUrl = buildOAuthUrl(scene);
        log.info("生成微信登录二维码，scene={}, oauthUrl={}", scene, oauthUrl);
        
        WechatQrCodeResponse response = new WechatQrCodeResponse();
        response.setQrCodeUrl(oauthUrl);
        response.setScene(scene);
        response.setExpiresIn(QR_CODE_EXPIRE);
        
        return response;
    }

    @Override
    public WechatQrStatusResponse checkQrStatus(String scene) {
        String redisKey = QR_CODE_STATUS_PREFIX + scene;
        String status = (String) redisTemplate.opsForValue().get(redisKey);
        
        if (status == null) {
            return new WechatQrStatusResponse("expired", null, "二维码已过期");
        }
        
        switch (status) {
            case "waiting":
                return new WechatQrStatusResponse("waiting", null, "等待扫码");
            case "scanned":
                return new WechatQrStatusResponse("scanned", null, "已扫码，等待确认");
            case "confirmed":
                String authCodeKey = AUTH_CODE_PREFIX + scene;
                String code = (String) redisTemplate.opsForValue().get(authCodeKey);
                if (code != null) {
                    redisTemplate.delete(redisKey);
                    redisTemplate.delete(authCodeKey);
                    try {
                        LoginResponse loginResponse = loginByAuthCode(code);
                        return new WechatQrStatusResponse("confirmed", loginResponse, "登录成功");
                    } catch (Exception e) {
                        log.error("微信登录失败", e);
                        return new WechatQrStatusResponse("expired", null, "登录失败：" + e.getMessage());
                    }
                }
                return new WechatQrStatusResponse("expired", null, "授权码已过期");
            default:
                return new WechatQrStatusResponse("expired", null, "二维码状态未知");
        }
    }

    @Override
    public WechatQrStatusResponse handleCallback(String code, String state) {
        log.info("微信回调，code={}, state={}", code, state);
        
        String authCodeKey = AUTH_CODE_PREFIX + state;
        redisTemplate.opsForValue().set(authCodeKey, code, 300, TimeUnit.SECONDS);
        
        String statusKey = QR_CODE_STATUS_PREFIX + state;
        redisTemplate.opsForValue().set(statusKey, "confirmed", 60, TimeUnit.SECONDS);
        
        return new WechatQrStatusResponse("confirmed", null, "已确认登录");
    }

    @Override
    public LoginResponse login(WechatLoginRequest request) {
        throw new BusinessException(1000, "H5 端请使用扫码登录");
    }
    
    private String buildOAuthUrl(String state) {
        String encodedRedirectUri = java.net.URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        
        return UriComponentsBuilder
            .fromHttpUrl("https://open.weixin.qq.com/connect/qrconnect")
            .queryParam("appid", appid)
            .queryParam("redirect_uri", encodedRedirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", "snsapi_login")
            .queryParam("state", state)
            .queryParam("connect_redirect", 1)
            .build()
            .toUriString();
    }
    
    private LoginResponse loginByAuthCode(String code) {
        try {
            String tokenUrl = String.format(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                appid, secret, code
            );
            
            ResponseEntity<String> tokenResponse = restTemplate.getForEntity(tokenUrl, String.class);
            JsonNode tokenNode = objectMapper.readTree(tokenResponse.getBody());
            
            if (tokenNode.has("errcode")) {
                log.error("获取 access_token 失败：{}", tokenNode);
                throw new BusinessException(1000, "获取微信授权失败");
            }
            
            String accessToken = tokenNode.get("access_token").asText();
            String openId = tokenNode.get("openid").asText();
            
            String userInfoUrl = String.format(
                "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN",
                accessToken, openId
            );
            
            ResponseEntity<String> userResponse = restTemplate.getForEntity(userInfoUrl, String.class);
            JsonNode userNode = objectMapper.readTree(userResponse.getBody());
            
            if (userNode.has("errcode")) {
                log.error("获取用户信息失败：{}", userNode);
                throw new BusinessException(1000, "获取微信用户信息失败");
            }
            
            String unionId = userNode.has("unionid") ? userNode.get("unionid").asText() : null;
            String nickname = userNode.get("nickname").asText();
            String avatarUrl = userNode.get("headimgurl").asText();
            String openid = userNode.get("openid").asText();
            
            return findOrCreateUser(openid, unionId, nickname, avatarUrl);
            
        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BusinessException(1000, "微信登录失败：" + e.getMessage());
        }
    }
    
    private LoginResponse findOrCreateUser(String openid, String unionid, String nickname, String avatarUrl) {
        User user = null;
        if (unionid != null) {
            user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWechatUnionid, unionid)
            );
        }
        
        if (user == null) {
            user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWechatOpenid, openid)
            );
        }
        
        if (user == null) {
            user = new User();
            user.setWechatOpenid(openid);
            user.setWechatUnionid(unionid);
            user.setNickname(nickname);
            user.setAvatarUrl(avatarUrl);
            user.setRole("lawyer");
            user.setStatus(1);
            userMapper.insert(user);
            log.info("创建新用户，userId={}, nickname={}", user.getId(), nickname);
        }
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getPhone());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(604800L);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatarUrl());
        userInfo.setRole(user.getRole());
        response.setUser(userInfo);
        
        return response;
    }
}
