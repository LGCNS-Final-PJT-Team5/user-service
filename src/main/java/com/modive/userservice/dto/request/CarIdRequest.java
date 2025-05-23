package com.modive.userservice.dto.request;

import lombok.Data;

@Data
public class CarIdRequest {
    private Long carId;

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }
}