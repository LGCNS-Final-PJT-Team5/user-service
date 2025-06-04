package com.modive.userservice.controller;

import com.modive.userservice.domain.UserInfo;
import com.modive.userservice.dto.request.*;
import com.modive.userservice.dto.response.*;
import com.modive.userservice.repository.UserRepository;
import com.modive.userservice.service.AdminService;
import com.modive.userservice.service.CarService;
import com.modive.userservice.service.UserService;
import com.modive.userservice.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CarService carService;
    private final AdminService adminService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> myInfo() {
        return new ApiResponse<>(HttpStatus.OK, userService.getUser());
    }

    @PatchMapping("/me/delete")
    public ApiResponse<String> withdraw() {
        return new ApiResponse<>(HttpStatus.OK, userService.deleteUser());
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserInfo> userInfoById(@PathVariable("userId") String userId) {
        return new ApiResponse<>(HttpStatus.OK, userService.getUserByUserId(userId));
    }

    @GetMapping
    public ApiResponse<UserInfo> userInfoByNickname(@RequestParam("search") String search) {
        return new ApiResponse<>(HttpStatus.OK, userService.getUserByNickname(search));
    }

    @PostMapping("/{userId}/delete")
    public ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        return new ApiResponse<>(HttpStatus.OK, userService.deleteUser(userId));
    }

    @GetMapping("/list")
    public ApiResponse<UserListResponse> getUserList() {
        return new ApiResponse<>(HttpStatus.OK, adminService.getUserList());
    }

    @PatchMapping("/nickname")
    public ApiResponse<String> updateNickname(
            @RequestBody NicknameRequest request
    ) {
        userService.updateNickname(request.getNickname());
        return new ApiResponse<>(HttpStatus.OK);
    }

    @PatchMapping("/alarm")
    public ApiResponse<String> updateUserAlarm(
            @RequestBody AlarmRequest alarm
    ) {
        userService.updateUserAlarm(alarm.isAlarm());
        return new ApiResponse<>(HttpStatus.OK);
    }

    @PostMapping("/{userId}/reward")
    public ApiResponse<String> updateUserReward(
            @PathVariable("userId") String userId,
            @RequestBody RewardRequest request
    ) {
        userService.updateUserReward(userId, request.getReward());
        return new ApiResponse<>(HttpStatus.OK);
    }

    @GetMapping("/car")
    public ApiResponse<CarListResponse> getCarList() {
        return new ApiResponse<>(HttpStatus.OK, carService.getCarList());
    }

    @PostMapping("/car")
    public ApiResponse<String> addCar(
            @RequestBody final CarNumberRequest request
    ) {
        carService.addCar(request.getNumber());
        return new ApiResponse<>(HttpStatus.OK);
    }

    @DeleteMapping("/car")
    public ApiResponse<String> deleteCar(
            @RequestBody final CarIdRequest request
    ) {
        carService.deleteCar(request.getCarId());
        return new ApiResponse<>(HttpStatus.OK);
    }

    @PatchMapping("/car")
    public ApiResponse<String> updateCar(
            @RequestBody final CarIdRequest request
    ) {
        carService.updateCar(request.getCarId());
        return new ApiResponse<>(HttpStatus.OK);
    }

    @GetMapping("/interest")
    public ApiResponse<String> getInterest() {
        return new ApiResponse<>(HttpStatus.OK, userService.getInterest());
    }

    @PatchMapping("/interest")
    public ApiResponse<String> updateInterest(
            @RequestBody final InterestRequest request
    ) {
        userService.updateUserInterest(request.getInterest());
        return new ApiResponse<>(HttpStatus.OK);
    }

    @GetMapping("/monthly-stats")
    public ApiResponse<MonthlyStatsResponse> getMonthlyChurnStats() {
        return new ApiResponse<>(HttpStatus.OK, adminService.getMonthlyStats());
    }

    @GetMapping("/search")
    public ApiResponse<SearchUserResponse> searchByEmail(@RequestParam("searchKeyword") String email) {
        return new ApiResponse<>(HttpStatus.OK, adminService.searchByEmail(email));
    }

    @GetMapping("/total")
    public ApiResponse<TotalUserChangeResponse> getTotalUser() {
        return new ApiResponse<>(HttpStatus.OK, adminService.getTotalUser());
    }

    @GetMapping("/total-cars")
    public ApiResponse<TotalCarChangeResponse> getTotalCar() {
        return new ApiResponse<>(HttpStatus.OK, adminService.getTotalCar());
    }

    @GetMapping("/filter")
    public ApiResponse<Page<UserInfo>> getUsers(
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer accountAgeInMonths,
            @RequestParam(required = false) Integer active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<UserInfo> userInfos = adminService.findUsers(
                minExperience,
                maxExperience,
                accountAgeInMonths,
                active,
                pageable
        );

        return new ApiResponse<>(HttpStatus.OK, userInfos);
    }
}
