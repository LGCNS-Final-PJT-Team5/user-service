package com.modive.userservice.service;

import com.modive.userservice.domain.User;
import com.modive.userservice.domain.UserInfo;
import com.modive.userservice.dto.response.*;
import com.modive.userservice.exception.UserNotFoundException;
import com.modive.userservice.repository.CarRepository;
import com.modive.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.data.jpa.domain.Specification;

class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CarRepository carRepository;

    @InjectMocks private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("searchByEmail - 이메일로 사용자 검색")
    void testSearchByEmail() {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setCreateDateTime(LocalDateTime.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);

        SearchUserResponse response = adminService.searchByEmail("test@example.com");

        assertNotNull(response);
        assertEquals(1, response.getSearchResult().size());
    }

    @Test
    @DisplayName("getTotalCar - Object[] 변환 실패 없도록 데이터 충분히 넣기")
    void testGetTotalCarWithVariousInput() {
        List<Object[]> rawData = new ArrayList<>();
        rawData.add(new Object[]{200L, 150L, 40L}); // 정상 케이스

        when(carRepository.countCars()).thenReturn(rawData);

        TotalCarChangeResponse response = adminService.getTotalCar();

        assertNotNull(response);
        assertEquals(200L, response.getTotalCarCount().getValue());
    }

    @Test
    @DisplayName("getMonthlyStats - 12개월 데이터가 정확히 생성되는지 확인")
    void testGetMonthlyStatsLength() throws Exception {
        when(userRepository.getNewUserCount()).thenReturn(10L);

        List<Object[]> userStats = new ArrayList<>();
        userStats.add(new Object[]{YearMonth.now().toString(), 5L});

        when(userRepository.getMonthlyUserStats()).thenReturn(userStats);
        when(userRepository.getMonthlyChurnStats()).thenReturn(new ArrayList<>());

        MonthlyStatsResponse response = adminService.getMonthlyStats();

        Field userStatsField = response.getClass().getDeclaredField("userStatistics");
        userStatsField.setAccessible(true);
        Object userStatistics = userStatsField.get(response);

        Field userTrendField = userStatistics.getClass().getDeclaredField("userTrend");
        userTrendField.setAccessible(true);
        Object[] userTrends = (Object[]) userTrendField.get(userStatistics);

        assertEquals(12, userTrends.length);  // 12개월 생성되었는지 확인
    }



    @Test
    @DisplayName("getUserList - 전체 사용자 목록 반환")
    void testGetUserList() {
        User user1 = new User();
        user1.setCreateDateTime(LocalDateTime.now());
        User user2 = new User();
        user2.setCreateDateTime(LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        UserListResponse response = adminService.getUserList();

        assertNotNull(response);
        assertEquals(2, response.getUserInfos().size()); // getUserList() → getUserInfos()
    }

    @Test
    @DisplayName("getTotalCar - 총 차량 추세 반환")
    void testGetTotalCar() {
        Long total = 100L;
        Long present = 10L;
        Long prior = 5L;

        long priorCount = total - present;        // 90
        long prior2Count = total - present - prior; // 85

        float expectedChangeRate = prior2Count == 0
                ? 1f
                : ((float) priorCount / prior2Count) - 1f;

        List<Object[]> carStats = new ArrayList<>();
        carStats.add(new Object[]{total, present, prior});

        when(carRepository.countCars()).thenReturn(carStats);

        TotalCarChangeResponse response = adminService.getTotalCar();

        assertNotNull(response);
        assertEquals(total, response.getTotalCarCount().getValue());
        assertEquals(expectedChangeRate, response.getTotalCarCount().getChangeRate(), 0.001f);
    }


    @Test
    @DisplayName("getMonthlyStats - 월별 사용자 활동 통계")
    void testGetMonthlyStats() throws Exception {
        when(userRepository.getNewUserCount()).thenReturn(5L);

        List<Object[]> userStats = new ArrayList<>();
        userStats.add(new Object[]{"2024-06", 3}); // 예: 내부 로직에서 12개월 분 생성

        List<Object[]> churnStats = new ArrayList<>();
        churnStats.add(new Object[]{"2024-06", 1});

        when(userRepository.getMonthlyUserStats()).thenReturn(userStats);
        when(userRepository.getMonthlyChurnStats()).thenReturn(churnStats);

        MonthlyStatsResponse response = adminService.getMonthlyStats();

        assertNotNull(response);

        // 리플렉션으로 내부 userStatistics.userTrend 가져오기
        Field userStatsField = response.getClass().getDeclaredField("userStatistics");
        userStatsField.setAccessible(true);
        Object userStatistics = userStatsField.get(response);

        Field userTrendField = userStatistics.getClass().getDeclaredField("userTrend");
        userTrendField.setAccessible(true);
        Object[] userTrends = (Object[]) userTrendField.get(userStatistics);

        assertEquals(12, userTrends.length);
    }

    @Test
    @DisplayName("getTotalCar - 분모 prior2Count가 0일 때")
    void testGetTotalCarWhenPrior2CountIsZero() {
        Long total = 100L;
        Long present = 90L;
        Long prior = 10L;

        // priorCount = 10, prior2Count = 0 → changeRate = 1f
        List<Object[]> carStats = new ArrayList<>();
        carStats.add(new Object[]{total, present, prior});

        when(carRepository.countCars()).thenReturn(carStats);

        TotalCarChangeResponse response = adminService.getTotalCar();

        assertNotNull(response);
        assertEquals(total, response.getTotalCarCount().getValue());
        assertEquals(1f, response.getTotalCarCount().getChangeRate(), 0.0001f);
    }

    @Test
    @DisplayName("getTotalUser - 총 사용자 변화 추이 계산 확인")
    void testGetTotalUser() {
        // given
        when(userRepository.getNewUserCount()).thenReturn(10L);

        List<Object[]> userStats = new ArrayList<>();
        userStats.add(new Object[]{"2024-05", 30L});
        userStats.add(new Object[]{"2024-06", 20L});

        List<Object[]> churnStats = new ArrayList<>();
        churnStats.add(new Object[]{"2024-06", 5L});

        when(userRepository.getMonthlyUserStats()).thenReturn(userStats);
        when(userRepository.getMonthlyChurnStats()).thenReturn(churnStats);

        // when
        TotalUserChangeResponse response = adminService.getTotalUser();

        // then
        assertNotNull(response);
        assertTrue(response.getTotalUserCount().getValue() >= 0);
    }


    @Test
    @DisplayName("searchByEmail - 사용자 없을 때 예외 발생")
    void testSearchByEmailUserNotFound() {
        when(userRepository.findByEmail("none@example.com")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            adminService.searchByEmail("none@example.com");
        });
    }

    @Test
    @DisplayName("getUserList - 사용자 없음")
    void testGetUserListEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        UserListResponse response = adminService.getUserList();

        assertNotNull(response);
        assertEquals(0, response.getUserInfos().size());
    }


    @Test
    @DisplayName("findUsers - 조건 기반 사용자 검색")
    void testFindUsers() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setCreateDateTime(LocalDateTime.now());
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        Page<UserInfo> result = adminService.findUsers(1, 10, 12, 1, pageable);
        assertEquals(1, result.getTotalElements());
    }
}
