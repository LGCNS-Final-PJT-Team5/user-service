package com.modive.userservice.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyStatsDto {
    private String monthYear;
    private Long count;

    public MonthlyStatsDto(String monthYear, Number count) {
        this.monthYear = monthYear;
        this.count = count != null ? count.longValue() : 0L;
    }
}
