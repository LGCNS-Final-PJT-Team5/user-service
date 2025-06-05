package com.modive.userservice.service;

import com.modive.userservice.domain.User;
import com.modive.userservice.domain.UserInfo;
import com.modive.userservice.dto.response.UserResponse;
import com.modive.userservice.repository.UserRepository;
import com.modive.userservice.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserUtil userContextUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private String testUserId;
    private String testNickname;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserId = "test-user-id";
        testNickname = "testuser";

        testUser = User.builder()
                .userId(testUserId)
                .name("테스트 사용자")
                .nickname(testNickname)
                .email("test@example.com")
                .reward(1000L)
                .interest("드라이브")
                .drivingExperience(5L)
                .alarm(true)
                .socialId("social123")
                .socialType("KAKAO")
                .isActive(true)
                .createDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("사용자 정보 조회 성공")
    void getUser_Success() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // when
        UserResponse result = userService.getUser();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getReward()).isEqualTo(testUser.getReward());
        assertThat(result.getNickname()).isEqualTo(testUser.getNickname());
        assertThat(result.getName()).isEqualTo(testUser.getName());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.isAlarm()).isEqualTo(testUser.isAlarm());

        verify(userContextUtil).getUserId();
        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 예외 발생")
    void getUser_UserNotFound_ThrowsException() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("닉네임으로 사용자 조회 성공")
    void getUserByNickname_Success() {
        // given
        when(userRepository.findByNickname(testNickname)).thenReturn(testUser);

        // when
        UserInfo result = userService.getUserByNickname(testNickname);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo(testNickname);
        assertThat(result.getUserId()).isEqualTo(testUserId);
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getExperience()).isEqualTo(testUser.getDrivingExperience());
        assertThat(result.getSeedBalance()).isEqualTo(testUser.getReward());

        verify(userRepository).findByNickname(testNickname);
    }

    @Test
    @DisplayName("닉네임으로 사용자 조회 - null 사용자")
    void getUserByNickname_NullUser() {
        // given
        when(userRepository.findByNickname(testNickname)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> userService.getUserByNickname(testNickname))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("사용자 ID로 사용자 조회 성공")
    void getUserByUserId_Success() {
        // given
        when(userRepository.findByUserId(testUserId)).thenReturn(testUser);

        // when
        UserInfo result = userService.getUserByUserId(testUserId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUserId);
        assertThat(result.getNickname()).isEqualTo(testNickname);
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getExperience()).isEqualTo(testUser.getDrivingExperience());
        assertThat(result.getSeedBalance()).isEqualTo(testUser.getReward());

        verify(userRepository).findByUserId(testUserId);
    }

    @Test
    @DisplayName("사용자 ID로 사용자 조회 - null 사용자")
    void getUserByUserId_NullUser() {
        // given
        when(userRepository.findByUserId(testUserId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> userService.getUserByUserId(testUserId))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("닉네임 업데이트 성공")
    void updateNickname_Success() {
        // given
        String newNickname = "새로운닉네임";
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findByUserId(testUserId)).thenReturn(testUser);

        // when
        userService.updateNickname(newNickname);

        // then
        assertThat(testUser.getNickname()).isEqualTo(newNickname);
        verify(userContextUtil).getUserId();
        verify(userRepository).findByUserId(testUserId);
        verify(userRepository).saveAndFlush(testUser);
    }

    @Test
    @DisplayName("닉네임 업데이트 - null 사용자")
    void updateNickname_NullUser() {
        // given
        String newNickname = "새로운닉네임";
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findByUserId(testUserId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> userService.updateNickname(newNickname))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("현재 사용자 삭제 성공")
    void deleteUser_CurrentUser_Success() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findByUserId(testUserId)).thenReturn(testUser);

        // when
        String result = userService.deleteUser();

        // then
        assertThat(result).isEqualTo("유저 삭제에 성공했습니다.");
        assertThat(testUser.isActive()).isFalse();
        verify(userContextUtil).getUserId();
        verify(userRepository).findByUserId(testUserId);
    }

    @Test
    @DisplayName("현재 사용자 삭제 실패")
    void deleteUser_CurrentUser_Failure() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findByUserId(testUserId)).thenThrow(new RuntimeException("Database error"));

        // when
        String result = userService.deleteUser();

        // then
        assertThat(result).isEqualTo("유저 삭제에 실패했습니다.");
    }

    @Test
    @DisplayName("특정 사용자 삭제 성공")
    void deleteUser_SpecificUser_Success() {
        // given
        String targetUserId = "target-user-id";
        when(userRepository.findByUserId(targetUserId)).thenReturn(testUser);

        // when
        String result = userService.deleteUser(targetUserId);

        // then
        assertThat(result).isEqualTo("유저 삭제에 성공했습니다.");
        assertThat(testUser.isActive()).isFalse();
        verify(userRepository).findByUserId(targetUserId);
    }

    @Test
    @DisplayName("특정 사용자 삭제 실패")
    void deleteUser_SpecificUser_Failure() {
        // given
        String targetUserId = "target-user-id";
        when(userRepository.findByUserId(targetUserId)).thenThrow(new RuntimeException("Database error"));

        // when
        String result = userService.deleteUser(targetUserId);

        // then
        assertThat(result).isEqualTo("유저 삭제에 실패했습니다.");
    }

    @Test
    @DisplayName("사용자 리워드 업데이트 성공")
    void updateUserReward_Success() {
        // given
        Long additionalReward = 500L;
        Long originalReward = testUser.getReward();
        when(userRepository.findByUserId(testUserId)).thenReturn(testUser);

        // when
        userService.updateUserReward(testUserId, additionalReward);

        // then
        assertThat(testUser.getReward()).isEqualTo(originalReward + additionalReward);
        verify(userRepository).findByUserId(testUserId);
        verify(userRepository).saveAndFlush(testUser);
    }

    @Test
    @DisplayName("사용자 리워드 업데이트 - null 사용자")
    void updateUserReward_NullUser() {
        // given
        Long additionalReward = 500L;
        when(userRepository.findByUserId(testUserId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> userService.updateUserReward(testUserId, additionalReward))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("사용자 알람 설정 업데이트 성공")
    void updateUserAlarm_Success() {
        // given
        boolean newAlarmSetting = false;
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // when
        userService.updateUserAlarm(newAlarmSetting);

        // then
        assertThat(testUser.isAlarm()).isEqualTo(newAlarmSetting);
        verify(userContextUtil).getUserId();
        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("사용자 알람 설정 업데이트 - 사용자 없음")
    void updateUserAlarm_UserNotFound_ThrowsException() {
        // given
        boolean newAlarmSetting = false;
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUserAlarm(newAlarmSetting))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("사용자 관심사 조회 성공")
    void getInterest_Success() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // when
        String result = userService.getInterest();

        // then
        assertThat(result).isEqualTo(testUser.getInterest());
        verify(userContextUtil).getUserId();
        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("사용자 관심사 조회 - 사용자 없음")
    void getInterest_UserNotFound_ThrowsException() {
        // given
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getInterest())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("사용자 관심사 업데이트 성공")
    void updateUserInterest_Success() {
        // given
        String newInterest = "여행";
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // when
        userService.updateUserInterest(newInterest);

        // then
        assertThat(testUser.getInterest()).isEqualTo(newInterest);
        verify(userContextUtil).getUserId();
        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("사용자 관심사 업데이트 - 사용자 없음")
    void updateUserInterest_UserNotFound_ThrowsException() {
        // given
        String newInterest = "여행";
        when(userContextUtil.getUserId()).thenReturn(testUserId);
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUserInterest(newInterest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }
}