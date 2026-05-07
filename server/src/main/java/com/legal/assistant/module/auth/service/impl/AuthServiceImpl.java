package com.legal.assistant.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.common.result.ResultCode;
import com.legal.assistant.common.security.JwtTokenProvider;
import com.legal.assistant.module.auth.dto.*;
import com.legal.assistant.module.auth.entity.User;
import com.legal.assistant.module.auth.mapper.UserMapper;
import com.legal.assistant.module.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final long SMS_CODE_EXPIRE = 5L;

    @Override
    public LoginResponse phoneLogin(PhoneLoginRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();

        validateSmsCode(phone, code);

        User user = getUserByPhone(phone);
        if (user == null) {
            user = createUser(phone);
        }

        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse emailLogin(EmailLoginRequest request) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail())
        );

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.AUTH_CREDENTIALS_ERROR);
        }

        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse register(EmailRegisterRequest request) {
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, request.getPhone())
                .or()
                .eq(User::getEmail, request.getEmail())
        );

        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        validateSmsCode(request.getPhone(), request.getCode());

        User user = new User();
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole("lawyer");
        user.setStatus(1);

        userMapper.insert(user);

        return buildLoginResponse(user);
    }

    @Override
    public void sendSmsCode(SendSmsRequest request) {
        String phone = request.getPhone();
        String code = generateSmsCode();

        String redisKey = SMS_CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(redisKey, code, SMS_CODE_EXPIRE, TimeUnit.MINUTES);

        log.info("发送短信验证码: phone={}, code={}", phone, code);

        // TODO: 实际调用短信服务发送验证码
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ResultCode.AUTH_TOKEN_INVALID);
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        return buildLoginResponse(user);
    }

    @Override
    public void logout(String userId) {
        // TODO: 将 token 加入黑名单
    }

    private User getUserByPhone(String phone) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getPhone, phone)
        );
    }

    private User createUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname("用户" + phone.substring(phone.length() - 4));
        user.setRole("lawyer");
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    private void validateSmsCode(String phone, String code) {
        String redisKey = SMS_CODE_PREFIX + phone;
        String cachedCode = (String) redisTemplate.opsForValue().get(redisKey);

        if (cachedCode == null) {
            throw new BusinessException(ResultCode.AUTH_SMS_CODE_EXPIRED);
        }

        if (!cachedCode.equals(code)) {
            throw new BusinessException(ResultCode.AUTH_SMS_CODE_ERROR);
        }

        redisTemplate.delete(redisKey);
    }

    private String generateSmsCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private LoginResponse buildLoginResponse(User user) {
        String token = jwtTokenProvider.generateToken(user.getId(), user.getPhone());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
            .id(user.getId())
            .phone(user.getPhone())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .avatar(user.getAvatarUrl())
            .role(user.getRole())
            .build();

        return LoginResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .expiresIn(604800L)
            .user(userInfo)
            .build();
    }
}