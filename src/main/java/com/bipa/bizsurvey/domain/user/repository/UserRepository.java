package com.bipa.bizsurvey.domain.user.repository;

import com.bipa.bizsurvey.domain.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    List<User> findByDelFlagFalseOrderByRegDateDesc(Pageable pageable);

    // 어드민 일주일 회원가입 통계
    @Query(value =
            "SELECT " +
                    "  CASE " +
                    "    WHEN DAYOFWEEK(calendar.date) = 1 THEN 'Sun' " +
                    "    WHEN DAYOFWEEK(calendar.date) = 2 THEN 'Mon' " +
                    "    WHEN DAYOFWEEK(calendar.date) = 3 THEN 'Tue' " +
                    "    WHEN DAYOFWEEK(calendar.date) = 4 THEN 'Wed' " +
                    "    WHEN DAYOFWEEK(calendar.date) = 5 THEN 'Thu' " +
                    "    WHEN DAYOFWEEK(calendar.date) = 6 THEN 'Fri' " +
                    "    WHEN DAYOFWEEK(calendar.date) = 7 THEN 'Sat' " +
                    "  END as dayOfWeek, " +
                    "  COALESCE(COUNT(users.email), 0) as signupCount " +
                    "FROM ( " +
                    "    SELECT CURDATE() - INTERVAL (a.a + (10 * b.a) + (100 * c.a)) DAY as date " +
                    "    FROM (SELECT 0 as a UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a " +
                    "    CROSS JOIN (SELECT 0 as a UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b " +
                    "    CROSS JOIN (SELECT 0 as a UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c " +
                    ") calendar " +
                    "LEFT JOIN users ON calendar.date = DATE(users.regdate) " +
                    "WHERE calendar.date BETWEEN CURDATE() - INTERVAL 6 DAY AND CURDATE() " +
                    "GROUP BY dayOfWeek " +
                    "ORDER BY FIELD(dayOfWeek, 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun')",
            nativeQuery = true)
    List<Object[]> getSignupStatistics();
}
