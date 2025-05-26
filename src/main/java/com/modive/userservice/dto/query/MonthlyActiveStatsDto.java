package com.modive.userservice.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyActiveStatsDto {
    private String monthYear;
    private Long count;
    private Long activeUsers;

    public MonthlyActiveStatsDto(String monthYear, Number count, Number activeUsers) {
        this.monthYear = monthYear;
        this.count = count != null ? count.longValue() : 0L;
        this.activeUsers = activeUsers != null ? activeUsers.longValue() : 0L;
    }
}
