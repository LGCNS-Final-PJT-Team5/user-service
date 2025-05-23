package com.modive.userservice.repository;

import com.modive.userservice.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByUserUserId(Long userId);

    Optional<Car> findByNumber(String number);

    Optional<Car> findByCarIdAndUserUserId(Long carId, Long userId);

    @Modifying
    @Query("UPDATE Car c SET c.active = false WHERE c.user.userId = :userId")
    void deactivateAllUserCars(@Param("userId") Long userId);

    // 특정 차량을 활성화
    @Modifying
    @Query("UPDATE Car c SET c.active = true WHERE c.carId = :carId AND c.user.userId = :userId")
    void activateUserCar(@Param("carId") Long carId, @Param("userId") Long userId);
}
