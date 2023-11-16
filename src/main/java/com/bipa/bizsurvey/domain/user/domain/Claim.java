package com.bipa.bizsurvey.domain.user.domain;


import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "claim")
public class Claim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimType claimType;


    @Column(nullable = false)
    private Long logicalKey;
    // 논리적 키

    @Enumerated(EnumType.STRING)
    private ClaimReason claimReason;
    // 신고 사유 Enum 처리해야함

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Claim(ClaimType claimType, Long logicalKey, ClaimReason claimReason, User user) {
        this.claimType = claimType;
        this.logicalKey = logicalKey;
        this.claimReason = claimReason;
        this.user = user;
    }
}
