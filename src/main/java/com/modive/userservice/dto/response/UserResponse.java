package com.modive.userservice.dto.response;

import com.modive.userservice.domain.User;
import com.modive.userservice.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long reward;
    private String nickname;
    private String name;
    private String email;
    private boolean alarm;

    public UserResponse(User user) {
        this.reward = user.getReward();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.email = user.getEmail();
        this.alarm = user.isAlarm();
    }

    public static UserResponse of(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        return UserResponse.builder()
                .reward(user.getReward())
                .nickname(user.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .alarm(user.isAlarm())
                .build();
    }
}
