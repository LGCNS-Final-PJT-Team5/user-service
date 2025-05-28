package com.modive.userservice.service;

import com.modive.userservice.domain.User;
import com.modive.userservice.domain.UserInfo;
import com.modive.userservice.dto.query.MonthlyActiveStatsDto;
import com.modive.userservice.dto.query.MonthlyStatsDto;
import com.modive.userservice.dto.query.TotalCarDto;
import com.modive.userservice.dto.response.*;
import com.modive.userservice.repository.CarRepository;
import com.modive.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public UserInfo searchByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return UserInfo.from(user);
    }

    public UserListResponse getUserList() {
        List<User> allUsers = userRepository.findAll();
        List<UserInfo> formattedUsers = allUsers.stream()
                .map(UserInfo::from)
                .toList();
        return UserListResponse.of(formattedUsers);
    }

    public TotalUserChangeResponse getTotalUser() {
        Long newUserCount = userRepository.getNewUserCount();

        List<YearMonth> last12Months = generateLast12Months();

        List<MonthlyStatsDto> userStats = convertToStats(userRepository.getMonthlyUserStats());
        List<MonthlyStatsDto> churnStats = convertToStats(userRepository.getMonthlyChurnStats());

        AtomicLong totalUser = new AtomicLong(0L);
        Map<String, MonthlyActiveStatsDto> userStatsMap = userStats.stream()
                .sorted(Comparator.comparing(MonthlyStatsDto::getMonthYear))
                .collect(Collectors.toMap(
                        MonthlyStatsDto::getMonthYear,
                        dto -> {
                            long cumulative = totalUser.addAndGet(dto.getCount());
                            return new MonthlyActiveStatsDto(dto.getMonthYear(), dto.getCount(), cumulative);
                        },
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new
                ));

        Map<String, Long> churnMap = churnStats.stream()
                .collect(Collectors.toMap(
                        MonthlyStatsDto::getMonthYear,
                        MonthlyStatsDto::getCount
                ));

        AtomicLong finalUser = new AtomicLong(0L);
        AtomicLong totalChurn = new AtomicLong(0L);
        AtomicLong priorUsers = new AtomicLong(0L);
        AtomicLong prior2Users = new AtomicLong(0L);

        List<UserTrend> userTrendList = last12Months.stream()
                .map(yearMonth -> {
                    String monthKey = yearMonth.toString();
                    prior2Users.set(priorUsers.get());
                    priorUsers.set(finalUser.get());
                    MonthlyActiveStatsDto userStat = userStatsMap.get(monthKey);
                    Long churnCount = churnMap.getOrDefault(monthKey, 0L);
                    long cumulative = totalChurn.addAndGet(churnCount);
                    if (userStat != null) {
                        finalUser.set(userStat.getActiveUsers() - cumulative);
                    } else {
                        finalUser.getAndAdd(cumulative * -1);
                    }

                    return new UserTrend(
                            (long) yearMonth.getYear(),
                            (long) yearMonth.getMonthValue(),
                            userStat != null ? userStat.getCount() : 0L,
                            userStat != null ? finalUser.get() : 0L,
                            churnCount
                    );
                })
                .toList();

        TotalChangeResponse totalChangeResponse =  TotalChangeResponse.of(
                finalUser.get(),
                prior2Users.get() == 0 ? 1f : (float) priorUsers.get() / prior2Users.get() - 1f
                );
        return TotalUserChangeResponse.of(totalChangeResponse);
    }

    public TotalCarChangeResponse getTotalCar() {
        TotalCarDto totalCarDto = convertToStats(carRepository.countCars().get(0));
        long priorCount = totalCarDto.getTotal() - totalCarDto.getPresent();
        long prior2Count = totalCarDto.getTotal() - totalCarDto.getPresent() - totalCarDto.getPrior();

        TotalChangeResponse totalChangeResponse =  TotalChangeResponse.of(
                totalCarDto.getTotal(),
                prior2Count == 0 ? 1f : (float) priorCount / prior2Count - 1f
        );

        return TotalCarChangeResponse.of(totalChangeResponse);
    }

    public MonthlyStatsResponse getMonthlyStats() {
        Long newUserCount = userRepository.getNewUserCount();

        List<YearMonth> last12Months = generateLast12Months();

        List<MonthlyStatsDto> userStats = convertToStats(userRepository.getMonthlyUserStats());
        List<MonthlyStatsDto> churnStats = convertToStats(userRepository.getMonthlyChurnStats());

        AtomicLong totalUser = new AtomicLong(0L);
        Map<String, MonthlyActiveStatsDto> userStatsMap = userStats.stream()
                .sorted(Comparator.comparing(MonthlyStatsDto::getMonthYear))
                .collect(Collectors.toMap(
                        MonthlyStatsDto::getMonthYear,
                        dto -> {
                            long cumulative = totalUser.addAndGet(dto.getCount());
                            return new MonthlyActiveStatsDto(dto.getMonthYear(), dto.getCount(), cumulative);
                        },
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new
                ));

        Map<String, Long> churnMap = churnStats.stream()
                .collect(Collectors.toMap(
                        MonthlyStatsDto::getMonthYear,
                        MonthlyStatsDto::getCount
                ));

        AtomicLong finalUser = new AtomicLong(0L);
        AtomicLong totalChurn = new AtomicLong(0L);
        AtomicLong priorUsers = new AtomicLong(0L);

        List<UserTrend> userTrendList = last12Months.stream()
                .map(yearMonth -> {
                    String monthKey = yearMonth.toString();
                    priorUsers.set(finalUser.get());
                    MonthlyActiveStatsDto userStat = userStatsMap.get(monthKey);
                    Long churnCount = churnMap.getOrDefault(monthKey, 0L);
                    long cumulative = totalChurn.addAndGet(churnCount);
                    if (userStat != null) {
                        finalUser.set(userStat.getActiveUsers() - cumulative);
                    } else {
                        finalUser.getAndAdd(cumulative * -1);
                    }

                    return new UserTrend(
                            (long) yearMonth.getYear(),
                            (long) yearMonth.getMonthValue(),
                            userStat != null ? userStat.getCount() : 0L,
                            userStat != null ? finalUser.get() : 0L,
                            churnCount
                    );
                })
                .toList();

        UserTrend[] userTrends = userTrendList.toArray(new UserTrend[0]);


        return MonthlyStatsResponse.of(
                newUserCount,
                priorUsers.get() == 0 ? 1f : (float) finalUser.get() / priorUsers.get() - 1f,
                (float) totalChurn.get() / totalUser.get(),
                userTrends
        );
    }

    private List<MonthlyStatsDto> convertToStats(List<Object[]> rawData) {
        return rawData.stream()
                .map(row -> new MonthlyStatsDto(
                        (String) row[0],           // monthYear
                        (Number) row[1]            // churnCount
                ))
                .collect(Collectors.toList());
    }

    private TotalCarDto convertToStats(Object[] rawData) {
        return new TotalCarDto(
            (Number) rawData[0],
            (Number) rawData[1],
            (Number) rawData[2]
        );
    }

    private List<YearMonth> generateLast12Months() {
        List<YearMonth> months = new ArrayList<>();
        YearMonth current = YearMonth.now();

        // 현재 월부터 11개월 전까지
        for (int i = 11; i >= 0; i--) {
            months.add(current.minusMonths(i));
        }

        return months;
    }

    public Page<UserInfo> findUsers(
            Integer minExperience,
            Integer maxExperience,
            Integer accountAgeInMonths,
            Integer active,
            Pageable pageable
    ) {
        // 동적 쿼리 생성을 위한 Specification 사용
        Specification<User> spec = Specification.where(null);

        if (minExperience != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("drivingExperience"), minExperience));
        }

        if (maxExperience != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("drivingExperience"), maxExperience));
        }

        if (accountAgeInMonths != null) {
            LocalDateTime threshold = LocalDateTime.now().minusMonths(accountAgeInMonths);
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createDateTime"), threshold));
        }

        if (active != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isActive"), active));
        }

        return userRepository.findAll(spec, pageable)
                .map(UserInfo::from);
    }
}
