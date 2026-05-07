package com.legal.assistant.module.user.service.impl;

import com.legal.assistant.common.exception.BusinessException;
import com.legal.assistant.common.result.ResultCode;
import com.legal.assistant.module.auth.entity.User;
import com.legal.assistant.module.auth.mapper.UserMapper;
import com.legal.assistant.module.user.dto.UpdatePasswordRequest;
import com.legal.assistant.module.user.dto.UpdateProfileRequest;
import com.legal.assistant.module.user.dto.UserProfileResponse;
import com.legal.assistant.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getProfile(String userId) {
        User user = getUserById(userId);
        return UserProfileResponse.fromEntity(user);
    }

    @Override
    public void updateProfile(String userId, UpdateProfileRequest request) {
        User user = getUserById(userId);

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatarUrl(request.getAvatar());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(String userId, UpdatePasswordRequest request) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.AUTH_CREDENTIALS_ERROR);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public User getUserById(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }
}