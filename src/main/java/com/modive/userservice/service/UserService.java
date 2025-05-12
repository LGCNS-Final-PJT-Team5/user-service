package com.modive.userservice.service;

import com.modive.userservice.domain.User;
import com.modive.userservice.dto.response.UserListResponse;
import com.modive.userservice.dto.response.UserResponse;
import com.modive.userservice.repository.UserRepository;
import com.modive.userservice.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public UserResponse getUser(final Long userId) {
        return UserResponse.of(userRepository.findByUserId(userId)
        );
    }

    public UserResponse getUser(final String nickname) {
        return UserResponse.of(userRepository.findByNickname(nickname));
    }

    public UserListResponse getAllUserNicknames() {
        List<User> allUsers = userRepository.findAll();
        List<String> allUserNicknames= allUsers.stream()
                .map(User::getNickname)
                .filter(Objects::nonNull) // null 닉네임 필터링
                .toList();
        return UserListResponse.of(allUserNicknames);
    }

    @Transactional
    public String updateNickname(final String nickname) {
        User user = userRepository.findByUserId(userContextUtil.getUserId());
        user.setNickname(nickname);
        return userRepository.saveAndFlush(user).getNickname();
    }

    @Transactional
    public String deleteUser() {
        Long userId = userContextUtil.getUserId();
        Long isDelete = userRepository.deleteUserByUserId(userId);
        if (Objects.equals(isDelete, userId)) {
            return "유저 삭제에 성공했습니다.";
        }
        else {
            return "유저 삭제에 실패했습니다.";
        }
    }

    @Transactional
    public String deleteUser(final Long userId) {
        Long isDelete = userRepository.deleteUserByUserId(userId);
        if (Objects.equals(isDelete, userId)) {
            return "유저 삭제에 성공했습니다.";
        }
        else {
            return "유저 삭제에 실패했습니다.";
        }
    }

    @Transactional
    public void updateUserReward(Long userId, Long reward) {
        User user = userRepository.findByUserId(userId);
        user.setReward(user.getReward() + reward);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void updateUserAlarm(Long userId, boolean alarm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAlarm(alarm);
    }
}
