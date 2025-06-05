package com.modive.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modive.userservice.dto.request.*;
import com.modive.userservice.dto.response.*;
import com.modive.userservice.domain.UserInfo;
import com.modive.userservice.service.AdminService;
import com.modive.userservice.service.CarService;
import com.modive.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller Unit Test")
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private CarService carService;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setHandlerExceptionResolvers()
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("GET /user/me - 내 정보 조회")
    void testMyInfo() throws Exception {
        // Given
        UserResponse userResponse = UserResponse.builder()
                .reward(1000L)
                .nickname("테스트유저")
                .name("김철수")
                .email("test@example.com")
                .alarm(true)
                .build();

        when(userService.getUser()).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.data.reward").value(1000))
                .andExpect(jsonPath("$.data.alarm").value(true));
    }

    @Test
    @DisplayName("PATCH /user/me/delete - 회원 탈퇴")
    void testWithdraw() throws Exception {
        // Given
        when(userService.deleteUser()).thenReturn("탈퇴 완료");

        // When & Then
        mockMvc.perform(patch("/user/me/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("탈퇴 완료"));
    }

    @Test
    @DisplayName("GET /user/{userId} - ID로 사용자 조회")
    void testGetUserById() throws Exception {
        // Given
        UserInfo userInfo = UserInfo.builder()
                .userId("test-user-id")
                .nickname("테스트유저")
                .email("test@example.com")
                .experience(5L)
                .joinedAt("2024-01-01")
                .seedBalance(1000L)
                .isActive(1L)
                .build();

        when(userService.getUserByUserId(anyString())).thenReturn(userInfo);

        // When & Then
        mockMvc.perform(get("/user/test-user-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value("test-user-id"))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"));
    }

    @Test
    @DisplayName("GET /user?search=nickname - 닉네임으로 조회")
    void testGetUserByNickname() throws Exception {
        // Given
        UserInfo userInfo = UserInfo.builder()
                .userId("test-user-id")
                .nickname("테스트유저")
                .email("test@example.com")
                .joinedAt("2024-01-01")
                .seedBalance(1000L)
                .isActive(1L)
                .build();

        when(userService.getUserByNickname(anyString())).thenReturn(userInfo);

        // When & Then
        mockMvc.perform(get("/user").param("search", "테스트유저"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"));
    }

    @Test
    @DisplayName("POST /user/{userId}/delete - 사용자 삭제")
    void testDeleteUser() throws Exception {
        // Given
        when(userService.deleteUser(anyString())).thenReturn("삭제됨");

        // When & Then
        mockMvc.perform(post("/user/123/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("삭제됨"));
    }

    @Test
    @DisplayName("GET /user/list - 사용자 리스트")
    void testGetUserList() throws Exception {
        // Given
        List<UserInfo> userInfos = new ArrayList<>();
        userInfos.add(UserInfo.builder()
                .userId("user1")
                .nickname("유저1")
                .email("user1@example.com")
                .joinedAt("2024-01-01")
                .seedBalance(1000L)
                .isActive(1L)
                .build());
        userInfos.add(UserInfo.builder()
                .userId("user2")
                .nickname("유저2")
                .email("user2@example.com")
                .joinedAt("2024-01-02")
                .seedBalance(2000L)
                .isActive(1L)
                .build());

        UserListResponse response = UserListResponse.of(userInfos);
        when(adminService.getUserList()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length").value(2))
                .andExpect(jsonPath("$.data.userInfos[0].nickname").value("유저1"));
    }

    @Test
    @DisplayName("PATCH /user/nickname - 닉네임 수정")
    void testUpdateNickname() throws Exception {
        // Given
        NicknameRequest request = new NicknameRequest("새닉네임");

        // When & Then
        mockMvc.perform(patch("/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PATCH /user/alarm - 알람 설정")
    void testUpdateAlarm() throws Exception {
        // Given
        AlarmRequest request = new AlarmRequest(true);

        // When & Then
        mockMvc.perform(patch("/user/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("POST /user/{userId}/reward - 리워드 설정")
    void testUpdateReward() throws Exception {
        // Given
        RewardRequest request = new RewardRequest();
        request.reward = 500L;

        // When & Then
        mockMvc.perform(post("/user/123/reward")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /user/car - 차량 목록 조회")
    void testGetCarList() throws Exception {
        // Given
        CarListResponse response = CarListResponse.builder()
                .length(2)
                .cars(new ArrayList<>())
                .build();

        when(carService.getCarList()).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/user/car"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length").value(2));
    }

    @Test
    @DisplayName("POST /user/car - 차량 추가")
    void testAddCar() throws Exception {
        // Given
        CarNumberRequest request = new CarNumberRequest("123가4567");

        // When & Then
        mockMvc.perform(post("/user/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /user/car - 차량 삭제")
    void testDeleteCar() throws Exception {
        // Given
        CarIdRequest request = new CarIdRequest("test-car-id");

        // When & Then
        mockMvc.perform(delete("/user/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PATCH /user/car - 차량 수정")
    void testUpdateCar() throws Exception {
        // Given
        CarIdRequest request = new CarIdRequest("test-car-id");

        // When & Then
        mockMvc.perform(patch("/user/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /user/interest - 관심사 조회")
    void testGetInterest() throws Exception {
        // Given
        when(userService.getInterest()).thenReturn("자동차");

        // When & Then
        mockMvc.perform(get("/user/interest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("자동차"));
    }

    @Test
    @DisplayName("PATCH /user/interest - 관심사 수정")
    void testUpdateInterest() throws Exception {
        // Given
        InterestRequest request = new InterestRequest("새관심사");

        // When & Then
        mockMvc.perform(patch("/user/interest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /user/search?searchKeyword= - 이메일 검색")
    void testSearchByEmail() throws Exception {
        // Given
        List<UserInfo> searchResults = new ArrayList<>();
        searchResults.add(UserInfo.builder()
                .userId("found-user")
                .nickname("발견된유저")
                .email("found@example.com")
                .joinedAt("2024-01-01")
                .seedBalance(1000L)
                .isActive(1L)
                .build());

        SearchUserResponse response = new SearchUserResponse(searchResults);
        when(adminService.searchByEmail(anyString())).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/user/search").param("searchKeyword", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.searchResult[0].nickname").value("발견된유저"));
    }

    @Test
    @DisplayName("HTTP Method 검증 - PUT은 지원하지 않음")
    void testUnsupportedHttpMethod() throws Exception {
        // When & Then
        mockMvc.perform(put("/user/me"))  // PATCH 대신 PUT 사용
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("JSON 요청 처리 검증")
    void testJsonRequestProcessing() throws Exception {
        // Given
        NicknameRequest request = new NicknameRequest("테스트닉네임");
        String json = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(patch("/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("요청 파라미터 처리 검증")
    void testRequestParameterProcessing() throws Exception {
        // Given
        UserInfo userInfo = UserInfo.builder()
                .userId("test-user")
                .nickname("테스트유저")
                .email("test@example.com")
                .joinedAt("2024-01-01")
                .seedBalance(1000L)
                .isActive(1L)
                .build();

        when(userService.getUserByNickname(anyString())).thenReturn(userInfo);

        // When & Then
        mockMvc.perform(get("/user").param("search", "테스트유저"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"));
    }
}