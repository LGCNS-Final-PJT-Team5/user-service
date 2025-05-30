package com.modive.userservice.service;

import com.modive.userservice.domain.User;
import com.modive.userservice.domain.UserInfo;
import com.modive.userservice.dto.response.UserListResponse;
import com.modive.userservice.dto.response.UserResponse;
import com.modive.userservice.repository.UserRepository;
import com.modive.userservice.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUtil userContextUtil;
    private final UserRepository userRepository;



    public UserResponse getUser() {
        User user = userRepository.findById(userContextUtil.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserResponse.of(user);
    }

    public UserInfo getUserByNickname(final String nickname) {
        return UserInfo.from(userRepository.findByNickname(nickname));
    }

    public UserInfo getUserByUserId(final String userId) {
        return UserInfo.from(userRepository.findByUserId(userId));
    }

    @Transactional
    public void updateNickname(final String nickname) {
        User user = userRepository.findByUserId(userContextUtil.getUserId());
        user.setNickname(nickname);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public String deleteUser() {
        try {
            String userId = userContextUtil.getUserId();
            User user = userRepository.findByUserId(userId);
            user.setActive(false);
            return "유저 삭제에 성공했습니다.";
        } catch (Exception e) {
            return "유저 삭제에 실패했습니다.";
        }
    }

    @Transactional
    public String deleteUser(final String userId) {
        try {
            User user = userRepository.findByUserId(userId);
            user.setActive(false);
            return "유저 삭제에 성공했습니다.";
        } catch (Exception e) {
            return "유저 삭제에 실패했습니다.";
        }
    }

    @Transactional
    public void updateUserReward(String userId, Long reward) {
        User user = userRepository.findByUserId(userId);
        user.setReward(user.getReward() + reward);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void updateUserAlarm(boolean alarm) {
        User user = userRepository.findById(userContextUtil.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAlarm(alarm);
    }

    public String getInterest() {
        User user = userRepository.findById(userContextUtil.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getInterest();
    }

    @Transactional
    public void updateUserInterest(String interest) {
        User user = userRepository.findById(userContextUtil.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setInterest(interest);
    }
}
