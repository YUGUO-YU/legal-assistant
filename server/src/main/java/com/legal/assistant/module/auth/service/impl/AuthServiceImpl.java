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
        user.setId(java.util.UUID.randomUUID().toString());
        user.setPhone(phone);
        user.setNickname("用户" + phone.substring(phone.length() - 4));
        user.setRole("lawyer");
        user.setStatus(1);
        user.setPasswordHash("$2a$10$dummy"); // 占位符，实际不会用到
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
    
    private void validateEmailCode(String email, String code) {
        String redisKey = EMAIL_CODE_PREFIX + email;
        String cachedCode = (String) redisTemplate.opsForValue().get(redisKey);
        
        if (cachedCode == null) {
            throw new BusinessException(1000, "验证码已过期或不存在");
        }
        
        if (!cachedCode.equals(code)) {
            throw new BusinessException(1000, "验证码错误");
        }
        
        redisTemplate.delete(redisKey);
    }
    
    public void sendEmailCode(String email) {
        String code = generateSmsCode();
        
        String redisKey = EMAIL_CODE_PREFIX + email;
        redisTemplate.opsForValue().set(redisKey, code, SMS_CODE_EXPIRE, TimeUnit.MINUTES);
        
        log.info("发送邮件验证码：email={}, code={}", email, code);
        
        // TODO: 实际调用邮件服务发送邮件
    }
    
    private User createByEmail(String email) {
        User user = new User();
        user.setId(java.util.UUID.randomUUID().toString());
        user.setEmail(email);
        String nickname = email.substring(0, email.indexOf("@"));
        user.setNickname(nickname);
        user.setRole("lawyer");
        user.setStatus(1);
        user.setPasswordHash("$2a$10$dummy");
        userMapper.insert(user);
        log.info("创建邮箱用户，userId={}, email={}", user.getId(), email);
        return user;
    }

    private String generateSmsCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
    
    private static final String EMAIL_CODE_PREFIX = "email:code:";
    
    @Override
    public void sendEmailCode(SendSmsRequest request) {
        // 这个方法已废弃，使用 sendEmailCode 直接处理
    }
    
    @Override
    public LoginResponse emailCodeLogin(EmailCodeLoginRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        
        validateEmailCode(email, code);
        
        // 查找用户
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getEmail, email)
        );
        
        if (user == null) {
            // 自动创建用户
            user = createByEmail(email);
        }
        
        return buildLoginResponse(user);
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

    @Override
    public User findOrCreateWechatUser(String openid, String unionid, WechatLoginRequest request) {
        String redisKey = "wechat:openid:" + openid;
        String userId = (String) redisTemplate.opsForValue().get(redisKey);

        if (userId != null) {
            User existingUser = userMapper.selectById(userId);
            if (existingUser != null) {
                return existingUser;
            }
        }

        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getWechatOpenid, openid)
        );

        if (user == null && unionid != null) {
            user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getWechatUnionid, unionid)
            );
        }

        if (user == null) {
            user = createWechatUser(openid, unionid);
        }

        redisTemplate.opsForValue().set(redisKey, user.getId(), 7, TimeUnit.DAYS);

        return user;
    }

    @Override
    public LoginResponse generateToken(User user) {
        return buildLoginResponse(user);
    }

    private User createWechatUser(String openid, String unionid) {
        User user = new User();
        user.setWechatOpenid(openid);
        user.setWechatUnionid(unionid);
        user.setNickname("微信用户" + openid.substring(openid.length() - 6));
        user.setRole("lawyer");
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    @Override
    public void updateUserPhone(String userId, String phone) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPhone(phone);
            userMapper.updateById(user);
            log.info("更新用户手机号：userId={}, phone={}", userId, phone);
        }
    }
}
