package com.modive.userservice.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CarIdRequest {
    private UUID carId;

    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }
}