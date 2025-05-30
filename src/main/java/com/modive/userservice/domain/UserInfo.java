package com.modive.userservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String userId;
    private String nickname;
    private String email;
    private Long experience;
    private String joinedAt;
    private Long seedBalance;
    private Long isActive;

    public static UserInfo from(User user) {
        return UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .experience(user.getDrivingExperience())
                .joinedAt(user.getCreateDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .seedBalance(user.getReward())
                .isActive(user.isActive() ? 1L : 0L)
                .build();
    }
}
