package com.bipa.bizsurvey.domain.user.repository;

import com.bipa.bizsurvey.domain.user.domain.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

}
