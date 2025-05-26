package com.modive.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalChangeResponse {
    private Long value;
    private Float changeRate;

    public static TotalChangeResponse of(final Long value, final Float changeRate) {
        return TotalChangeResponse.builder()
                .value(value)
                .changeRate(changeRate)
                .build();
    }
}
