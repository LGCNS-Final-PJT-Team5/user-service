package com.modive.userservice.controller;

import com.modive.userservice.dto.request.*;
import com.modive.userservice.dto.response.ApiResponse;
import com.modive.userservice.dto.response.CarListResponse;
import com.modive.userservice.dto.response.UserListResponse;
import com.modive.userservice.dto.response.UserResponse;
import com.modive.userservice.repository.UserRepository;
import com.modive.userservice.service.AdminService;
import com.modive.userservice.service.CarService;
import com.modive.userservice.service.UserService;
import com.modive.userservice.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    private final CarService carService;
    private final AdminService adminService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> myInfo() {
        return new ApiResponse<>(HttpStatus.OK, userService.getUser());
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> userInfoById(@PathVariable("userId") Long userId) {
        return new ApiResponse<>(HttpStatus.OK, userService.getUser(userId));
    }

    @GetMapping
    public ApiResponse<UserResponse> userInfoByNickname(@RequestParam("search") String search) {
        return new ApiResponse<>(HttpStatus.OK, userService.getUser(search));
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable("userId") Long userId) {
        return new ApiResponse<>(HttpStatus.OK, userService.deleteUser(userId));

    }

    @GetMapping("/list")
    public ApiResponse<UserListResponse> getUserList() {
        return new ApiResponse<>(HttpStatus.OK, adminService.getUserList());
    }

    @GetMapping("/nickname")
    public ApiResponse<Boolean> nicknameDuplicateCheck(@RequestParam("search") String nickname) {
        return new ApiResponse<>(HttpStatus.OK, userRepository.existsByNickname(nickname));
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
            @PathVariable("userId") Long userId,
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
}