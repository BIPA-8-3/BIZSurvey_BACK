package com.bipa.bizsurvey.domain.admin.dto.claim;

import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimListResponse {
    private Long id;
    private String claimType;
    private Long logicalKey;
    private String claimReason;
    private Long userId;
    private String userName;
    private LocalDateTime regDate;

    public ClaimListResponse(Long id, ClaimType claimType, Long logicalKey, ClaimReason claimReason, Long userId, String userName, LocalDateTime regDate) {
        this.id = id;
        this.claimType = claimType.getValue();
        this.logicalKey = logicalKey;
        this.claimReason = claimReason.getValue();
        this.userId = userId;
        this.userName = userName;
        this.regDate = regDate;
    }
}
