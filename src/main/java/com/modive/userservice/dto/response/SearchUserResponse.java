package com.modive.userservice.dto.response;
import com.modive.userservice.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserResponse {
    private List<UserInfo> searchResult;
}

