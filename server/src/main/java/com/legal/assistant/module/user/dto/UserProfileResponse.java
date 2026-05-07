package com.legal.assistant.module.user.dto;

import com.legal.assistant.module.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String phone;
    private String email;
    private String nickname;
    private String avatar;
    private String role;
    private String teamName;
    private String teamRole;
    private String createdAt;

    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
            .id(user.getId())
            .phone(user.getPhone())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .avatar(user.getAvatarUrl())
            .role(user.getRole())
            .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
            .build();
    }
}