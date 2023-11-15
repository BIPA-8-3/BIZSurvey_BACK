package com.bipa.bizsurvey.domain.user.repository;

import com.bipa.bizsurvey.domain.user.domain.Claim;
import com.bipa.bizsurvey.domain.user.dto.mypage.ClaimResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    @Query("select new com.bipa.bizsurvey.domain.user.dto.mypage.ClaimResponse" +
            "(c.id, c.claimType, c.logicalKey, c.claimReason, c.user.id, c.user.name)" +
            "from Claim c " +
            "join c.user " +
            "where c.user.id = :userId")
    List<ClaimResponse> findAllByWithUser(@Param("userId") Long userId);
}
