package com.modive.userservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarInfo {
    private String carId;
    private String number;
    private boolean active;

    public static CarInfo from(Car car) {
        return CarInfo.builder()
                .carId(car.getCarId())
                .number(car.getNumber())
                .active(car.isActive())
                .build();
    }
}
