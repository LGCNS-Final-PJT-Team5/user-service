package com.modive.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modive.userservice.dto.request.*;
import com.modive.userservice.dto.response.*;
import com.modive.userservice.domain.UserInfo;
import com.modive.userservice.service.AdminService;
import com.modive.userservice.service.CarService;
import com.modive.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private CarService carService;
    @MockBean private AdminService adminService;

    @MockBean
    private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;


    @Test
    @DisplayName("GET /user/me - 내 정보")
    void testMyInfo() throws Exception {
        Mockito.when(userService.getUser()).thenReturn(new UserResponse());

        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /user/me/delete - 회원 탈퇴")
    void testWithdraw() throws Exception {
        Mockito.when(userService.deleteUser()).thenReturn("탈퇴 완료");

        mockMvc.perform(patch("/user/me/delete"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/{userId} - ID로 사용자 조회")
    void testGetUserById() throws Exception {
        Mockito.when(userService.getUserByUserId(anyString())).thenReturn(new UserInfo());

        mockMvc.perform(get("/user/123"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user?search=nickname - 닉네임으로 조회")
    void testGetUserByNickname() throws Exception {
        Mockito.when(userService.getUserByNickname(anyString())).thenReturn(new UserInfo());

        mockMvc.perform(get("/user").param("search", "nickname"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /user/{userId}/delete - 사용자 삭제")
    void testDeleteUser() throws Exception {
        Mockito.when(userService.deleteUser(anyString())).thenReturn("삭제됨");

        mockMvc.perform(post("/user/123/delete"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/list - 사용자 리스트")
    void testGetUserList() throws Exception {
        Mockito.when(adminService.getUserList()).thenReturn(new UserListResponse());

        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /user/nickname - 닉네임 수정")
    void testUpdateNickname() throws Exception {
        NicknameRequest req = new NicknameRequest("newNick");

        mockMvc.perform(patch("/user/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /user/alarm - 알람 설정")
    void testUpdateAlarm() throws Exception {
        AlarmRequest req = new AlarmRequest(true);

        mockMvc.perform(patch("/user/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /user/{userId}/reward - 리워드 설정")
    void testUpdateReward() throws Exception {
        RewardRequest req = new RewardRequest(500L);

        mockMvc.perform(post("/user/123/reward")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/car - 차량 목록")
    void testGetCarList() throws Exception {
        Mockito.when(carService.getCarList()).thenReturn(new CarListResponse());

        mockMvc.perform(get("/user/car"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /user/car - 차량 추가")
    void testAddCar() throws Exception {
        CarNumberRequest req = new CarNumberRequest("123가4567");

        mockMvc.perform(post("/user/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /user/car - 차량 삭제")
    void testDeleteCar() throws Exception {
        CarIdRequest req = new CarIdRequest(UUID.randomUUID().toString());

        mockMvc.perform(delete("/user/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /user/car - 차량 수정")
    void testUpdateCar() throws Exception {
        CarIdRequest req = new CarIdRequest(UUID.randomUUID().toString());

        mockMvc.perform(patch("/user/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/interest - 관심사 조회")
    void testGetInterest() throws Exception {
        Mockito.when(userService.getInterest()).thenReturn("test");

        mockMvc.perform(get("/user/interest"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /user/interest - 관심사 수정")
    void testUpdateInterest() throws Exception {
        InterestRequest req = new InterestRequest("관심사");

        mockMvc.perform(patch("/user/interest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/monthly-stats - 월별 이탈 통계")
    void testGetMonthlyStats() throws Exception {
        Mockito.when(adminService.getMonthlyStats()).thenReturn(new MonthlyStatsResponse());

        mockMvc.perform(get("/user/monthly-stats"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/search?searchKeyword= - 이메일 검색")
    void testSearchByEmail() throws Exception {
        Mockito.when(adminService.searchByEmail(anyString())).thenReturn(new SearchUserResponse());

        mockMvc.perform(get("/user/search").param("searchKeyword", "test@test.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/total - 전체 유저 변화량")
    void testGetTotalUser() throws Exception {
        Mockito.when(adminService.getTotalUser()).thenReturn(new TotalUserChangeResponse());

        mockMvc.perform(get("/user/total"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/total-cars - 전체 차량 변화량")
    void testGetTotalCar() throws Exception {
        Mockito.when(adminService.getTotalCar()).thenReturn(new TotalCarChangeResponse());

        mockMvc.perform(get("/user/total-cars"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /user/filter - 필터 유저 목록")
    void testFilterUsers() throws Exception {
        Mockito.when(adminService.findUsers(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(new UserInfo())));

        mockMvc.perform(get("/user/filter")
                        .param("minExperience", "1")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }
}
