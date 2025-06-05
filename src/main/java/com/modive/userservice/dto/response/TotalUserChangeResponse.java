package com.modive.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalUserChangeResponse {
    private TotalChangeResponse totalUserCount;

    public static TotalUserChangeResponse of(TotalChangeResponse totalUserCount) {
        return TotalUserChangeResponse.builder()
                .totalUserCount(totalUserCount)
                .build();
    }
}
