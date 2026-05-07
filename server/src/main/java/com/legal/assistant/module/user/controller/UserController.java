package com.legal.assistant.module.user.controller;

import com.legal.assistant.common.result.Result;
import com.legal.assistant.module.user.dto.UpdatePasswordRequest;
import com.legal.assistant.module.user.dto.UpdateProfileRequest;
import com.legal.assistant.module.user.dto.UserProfileResponse;
import com.legal.assistant.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserProfileResponse> getProfile(@AuthenticationPrincipal String userId) {
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(
            @AuthenticationPrincipal String userId,
            @RequestBody UpdateProfileRequest request) {
        userService.updateProfile(userId, request);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(
            @AuthenticationPrincipal String userId,
            @RequestBody @Valid UpdatePasswordRequest request) {
        userService.updatePassword(userId, request);
        return Result.success();
    }
}