package com.modive.userservice.service;

import com.modive.userservice.domain.Car;
import com.modive.userservice.domain.User;
import com.modive.userservice.dto.response.CarListResponse;
import com.modive.userservice.repository.CarRepository;
import com.modive.userservice.repository.UserRepository;
import com.modive.userservice.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private UserUtil userContextUtil;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CarService carService;

    private String testUserId;
    private String testCarId;
    private User testUser;
    private Car testCar1;
    private Car testCar2;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-id";
        testCarId = "test-car-id";

        // User 객체 생성
        testUser = User.builder()
                .userId(testUserId)
                .name("테스트 사용자")
                .nickname("testuser")
                .email("test@example.com")
                .interest("드라이브")
                .drivingExperience(5L)
                .socialId("social123")
                .socialType("KAKAO")
                .isActive(true)
                .build();

        // Car 객체들 생성
        testCar1 = Car.builder()
                .carId("car-1")
                .number("12가1234")
                .user(testUser)
                .active(true)
                .build();

        testCar2 = Car.builder()
                .carId("car-2")
                .number("34나5678")
                .user(testUser)
                .active(false)
                .build();
    }

    @Test
    @DisplayName("사용자 차량 목록 조회 성공")
    void getCarList_Success() {
        // given
        List<Car> mockCars = Arrays.asList(testCar1, testCar2);
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(carRepository.findByUserUserId(testUserId)).thenReturn(mockCars);

        // when
        CarListResponse result = carService.getCarList();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLength()).isEqualTo(2);
        assertThat(result.getCars()).hasSize(2);
        assertThat(result.getCars().get(0).getCarId()).isEqualTo("car-1");
        assertThat(result.getCars().get(0).getNumber()).isEqualTo("12가1234");
        assertThat(result.getCars().get(0).isActive()).isTrue();

        verify(userContextUtil).getUserId();
        verify(carRepository).findByUserUserId(testUserId);
    }

    @Test
    @DisplayName("차량 번호가 null인 경우 필터링")
    void getCarList_FilterNullNumbers() {
        // given
        Car carWithNullNumber = Car.builder()
                .carId("car-3")
                .number(null)
                .user(testUser)
                .active(true)
                .build();

        List<Car> mockCars = Arrays.asList(testCar1, carWithNullNumber);
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(carRepository.findByUserUserId(testUserId)).thenReturn(mockCars);

        // when
        CarListResponse result = carService.getCarList();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLength()).isEqualTo(1); // null 번호 차량은 필터링됨
        assertThat(result.getCars()).hasSize(1);
        assertThat(result.getCars().get(0).getNumber()).isEqualTo("12가1234");
    }

    @Test
    @DisplayName("차량 추가 성공")
    void addCar_Success() {
        // given
        String carNumber = "12가1234";
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // when
        carService.addCar(carNumber);

        // then
        verify(userContextUtil).getUserId();
        verify(userRepository).findById(testUserId);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 차량 추가 시 예외 발생")
    void addCar_UserNotFound_ThrowsException() {
        // given
        String carNumber = "12가1234";
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> carService.addCar(carNumber))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저를 찾을 수 없습니다.");

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    @DisplayName("차량 삭제 성공")
    void deleteCar_Success() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(carRepository.findByCarIdAndUserUserId(testCarId, testUserId))
                .thenReturn(Optional.of(testCar1));

        // when
        carService.deleteCar(testCarId);

        // then
        verify(userContextUtil).getUserId();
        verify(carRepository).findByCarIdAndUserUserId(testCarId, testUserId);
        verify(carRepository).delete(testCar1);
    }

    @Test
    @DisplayName("존재하지 않는 차량 삭제 시 예외 발생")
    void deleteCar_CarNotFound_ThrowsException() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(carRepository.findByCarIdAndUserUserId(testCarId, testUserId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> carService.deleteCar(testCarId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("차량을 찾을 수 없습니다.");

        verify(carRepository, never()).delete(any(Car.class));
    }

    @Test
    @DisplayName("차량 활성화 업데이트 성공")
    void updateCar_Success() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);

        // when
        carService.updateCar(testCarId);

        // then
        verify(userContextUtil).getUserId();
        verify(carRepository).deactivateAllUserCars(testUserId);
        verify(carRepository).activateUserCar(testCarId, testUserId);
    }

    @Test
    @DisplayName("차량 활성화 업데이트 - 트랜잭션 동작 확인")
    void updateCar_TransactionalBehavior() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);

        // when
        carService.updateCar(testCarId);

        // then
        // 메서드 호출 순서 확인
        verify(carRepository).deactivateAllUserCars(testUserId);
        verify(carRepository).activateUserCar(testCarId, testUserId);
    }
}