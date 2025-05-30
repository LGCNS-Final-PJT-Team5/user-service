package com.modive.userservice.repository;

import com.modive.userservice.domain.User;
import com.modive.userservice.domain.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);

    Optional<User> findBySocialIdAndSocialType(String socialId, String socialType);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    User findByUserId(String userId);

    User findByNickname(String nickname);

    List<User> findAll();

    Long deleteUserByUserId(String userId);

    @Query(value = """
        SELECT
            DATE_FORMAT(create_date_time, '%Y-%m') AS monthYear,
            COUNT(*) AS count
        FROM users
        GROUP BY DATE_FORMAT(create_date_time, '%Y-%m')
        ORDER BY monthYear
        """, nativeQuery = true)
    List<Object[]> getMonthlyUserStats();

    @Query(value = """
        SELECT
            DATE_FORMAT(update_date_time, '%Y-%m') AS monthYear,
            SUM(CASE WHEN is_active = false THEN 1 ELSE 0 END) AS count
        FROM users
        GROUP BY DATE_FORMAT(update_date_time, '%Y-%m')
        ORDER BY monthYear
        """, nativeQuery = true)
    List<Object[]> getMonthlyChurnStats();

    @Query(value = """
        SELECT
            COUNT(*) AS count
        FROM users
        WHERE DATE(create_date_time) >= CURDATE() - WEEKDAY(CURDATE()) - 7
          AND DATE(create_date_time) < CURDATE() - WEEKDAY(CURDATE())
        """, nativeQuery = true)
    Long getNewUserCount();

    Page<User> findAll(Specification<User> spec, Pageable pageable);
}
