package com.modive.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalCarChangeResponse {
    private TotalChangeResponse totalCarCount;

    public static TotalCarChangeResponse of(TotalChangeResponse totalCarCount) {
        return TotalCarChangeResponse.builder()
                .totalCarCount(totalCarCount)
                .build();
    }
}
