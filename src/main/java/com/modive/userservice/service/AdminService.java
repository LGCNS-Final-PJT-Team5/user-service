package com.modive.userservice.service;

import com.modive.userservice.domain.CarInfo;
import com.modive.userservice.domain.User;
import com.modive.userservice.domain.UserInfo;
import com.modive.userservice.dto.response.UserListResponse;
import com.modive.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public UserListResponse getUserList() {
        List<User> allUsers = userRepository.findAll();
        List<UserInfo> formattedUsers = allUsers.stream()
                .map(UserInfo::from)
                .toList();
        return UserListResponse.of(formattedUsers);
    }

}
