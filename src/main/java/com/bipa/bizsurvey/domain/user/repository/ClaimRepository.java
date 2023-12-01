package com.bipa.bizsurvey.domain.user.repository;

import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimListResponse;
import com.bipa.bizsurvey.domain.admin.dto.claim.ClaimUserResponse;
import com.bipa.bizsurvey.domain.user.domain.Claim;
import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByUserId(Long userId);

    @Query("select new com.bipa.bizsurvey.domain.admin.dto.claim.ClaimListResponse" +
            "(c.id, c.claimType, c.logicalKey, c.claimReason, c.user.id, c.user.name)" +
            "from Claim c " +
            "join c.user " +
            "where c.delFlag = false and c.processing = :processing")
    List<ClaimListResponse> findAllByWithUser(@Param("processing") boolean processing);

    int countByPenalizedAndClaimReason(Long penalized, ClaimReason claimReason);


//    @Query("SELECT new com.bipa.bizsurvey.domain.admin.dto.claim.ClaimDetailResponse" +
//            "(c.id, c.claimType, c.logicalKey, c.claimReason, u.id, u.name, " +
//            "(SELECT p.id FROM Post p WHERE p.id = c.logicalKey), " +
//            "(SELECT p.title FROM Post p WHERE p.id = c.logicalKey), " +
//            "(SELECT u.id FROM User u WHERE u.id = c.logicalKey)) " +
//            "FROM Claim c " +
//            "LEFT JOIN User u ON u.id = c.user.id " +
//            "WHERE c.id = :claimId")
//    List<ClaimDetailResponse> findAllByPost(@Param("claimId") Long claimId);




//    @Query("select new com.bipa.bizsurvey.domain.admin.dto.claim.ClaimListResponse" +
//            "(c.id, c.claimType, c.logicalKey, c.claimReason, c.user.id, c.user.name)" +
//            "from Claim c " +
//            "join c.user " +
//            "where c.id = :id")
//    List<ClaimListResponse> findByIdWithUser(@Param("id") Long id);

}
