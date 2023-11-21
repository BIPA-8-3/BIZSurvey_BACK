package com.bipa.bizsurvey.domain.user.repository;

import com.bipa.bizsurvey.domain.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    List<User> findByDelFlagFalseOrderByRegDateDesc(Pageable pageable);

}
