package com.modive.userservice.service;

import com.modive.userservice.domain.Car;
import com.modive.userservice.domain.User;
import com.modive.userservice.domain.CarInfo;

import com.modive.userservice.dto.response.CarListResponse;
import com.modive.userservice.repository.CarRepository;
import com.modive.userservice.repository.UserRepository;
import com.modive.userservice.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final UserUtil userContextUtil;

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public CarListResponse getCarList() {
        Long userId = userContextUtil.getUserId();
        List<Car> usersCars = carRepository.findByUserUserId(userId);
        List<CarInfo> usersCarNumbers = usersCars.stream()
                .filter(car -> car.getNumber() != null)
                .map(CarInfo::from)
                .toList();
        return CarListResponse.of(usersCarNumbers);
    }

    public void addCar(String number) {
        Long userId = userContextUtil.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Car car = new Car();
        car.setUser(user);
        car.setNumber(number);
        car.setActive(false);
        carRepository.save(car);
    }

    public void deleteCar(Long carId) {
        Long userId = userContextUtil.getUserId();
        Car car = carRepository.findByCarIdAndUserUserId(carId, userId)
                .orElseThrow(() -> new IllegalArgumentException("차량을 찾을 수 없습니다."));
        carRepository.delete(car);
    }

    @Transactional
    public void updateCar(Long carId) {
        Long userId = userContextUtil.getUserId();
        carRepository.deactivateAllUserCars(userId);
        carRepository.activateUserCar(carId, userId);
    }
}
