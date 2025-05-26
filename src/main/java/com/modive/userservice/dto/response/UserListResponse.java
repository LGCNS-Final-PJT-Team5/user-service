package com.modive.userservice.dto.response;

import com.modive.userservice.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {

    private int length;
    private List<UserInfo> userInfos;

    public UserListResponse(List<UserInfo> userInfos) {
        this.length = userInfos.size();
        this.userInfos = userInfos;
    }

    public static UserListResponse of(List<UserInfo> userInfos) {
        return UserListResponse.builder()
                .length(userInfos.size())
                .userInfos(userInfos)
                .build();

    }
}
