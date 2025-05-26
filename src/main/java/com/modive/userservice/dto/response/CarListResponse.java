package com.modive.userservice.dto.response;

import com.modive.userservice.domain.CarInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarListResponse {

    private int length;
    private List<CarInfo> cars;

    public CarListResponse(List<CarInfo> cars) {
        this.length = cars.size();
        this.cars = cars;
    }

    public static CarListResponse of(List<CarInfo> cars) {
        return CarListResponse.builder()
                .length(cars.size())
                .cars(cars)
                .build();

    }
}
