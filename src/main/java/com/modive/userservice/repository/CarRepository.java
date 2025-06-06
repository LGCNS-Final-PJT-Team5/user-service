package com.modive.userservice.repository;

import com.modive.userservice.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, String> {

    List<Car> findByUserUserId(String userId);

    Optional<Car> findByNumber(String number);

    Optional<Car> findByCarIdAndUserUserId(String carId, String userId);

    @Modifying
    @Query("UPDATE Car c SET c.active = false WHERE c.user.userId = :userId")
    void deactivateAllUserCars(@Param("userId") String userId);

    // 특정 차량을 활성화
    @Modifying
    @Query("UPDATE Car c SET c.active = true WHERE c.carId = :carId AND c.user.userId = :userId")
    void activateUserCar(@Param("carId") String carId, @Param("userId") String userId);


    @Query(value = """
    SELECT
        COUNT(*) AS total,
        SUM(CASE WHEN MONTH(DATE(create_date_time)) = MONTH(CURDATE()) THEN 1 ELSE 0 END) AS present,
        SUM(CASE WHEN MONTH(DATE(create_date_time)) = (MONTH(CURDATE()) + 10) % 12 + 1 THEN 1 ELSE 0 END) AS prior
    FROM cars
    """, nativeQuery = true)
    List<Object[]> countCars();

}
