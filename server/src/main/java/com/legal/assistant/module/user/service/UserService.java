package com.legal.assistant.module.user.service;

import com.legal.assistant.module.auth.entity.User;
import com.legal.assistant.module.user.dto.UpdatePasswordRequest;
import com.legal.assistant.module.user.dto.UpdateProfileRequest;
import com.legal.assistant.module.user.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(String userId);
    void updateProfile(String userId, UpdateProfileRequest request);
    void updatePassword(String userId, UpdatePasswordRequest request);
    User getUserById(String userId);
}