package com.modive.userservice.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CarIdRequest {
    private String carId;

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }
}