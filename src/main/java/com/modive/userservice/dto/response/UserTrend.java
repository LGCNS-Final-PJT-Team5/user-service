package com.modive.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTrend {
    private Long year;
    private Long month;
    private Long newUsers;
    private Long activeUsers;
    private Long churnedUsers;
}
