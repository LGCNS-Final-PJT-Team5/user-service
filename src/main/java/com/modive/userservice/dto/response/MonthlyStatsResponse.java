package com.modive.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserStatistics {
    private Summary summary;
    private UserTrend[] userTrend;


    public static UserStatistics of(Summary summary, UserTrend[] userTrend) {
        return UserStatistics.builder()
                .summary(summary)
                .userTrend(userTrend)
                .build();
    }
}

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Summary {
    private Long lastWeekNewUsers;
    private float monthlyUserGrowthRate;
    private float churnRate;

    public static Summary of(Long lastWeekNewUsers,
                   float monthlyUserGrowthRate,
                   float churnRate) {
        return Summary.builder()
                .lastWeekNewUsers(lastWeekNewUsers)
                .monthlyUserGrowthRate(monthlyUserGrowthRate)
                .churnRate(churnRate)
                .build();
    }
}

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsResponse {
    private UserStatistics userStatistics;

    public static MonthlyStatsResponse of(
            Long lastWeekNewUsers,
            float monthlyUserGrowthRate,
            float churnRate,
            UserTrend[] userTrends
    ) {
        Summary summary = Summary.of(lastWeekNewUsers, monthlyUserGrowthRate, churnRate);

        return MonthlyStatsResponse.builder()
                .userStatistics(UserStatistics.of(summary, userTrends))
                .build();
    }
}
