package com.bipa.bizsurvey.domain.user.repository;

import com.bipa.bizsurvey.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
