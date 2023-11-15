package com.bipa.bizsurvey.domain.user.dto.mypage;

import com.bipa.bizsurvey.domain.user.enums.ClaimList;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClaimResponse {
    private Long id;
    private ClaimType claimType;
    private Long logicalKey;
    private ClaimList claimReason;
    private Long userId;
    private String userName;

    public ClaimResponse(Long id, ClaimType claimType, Long logicalKey, ClaimList claimReason, Long userId, String userName) {
        this.id = id;
        this.claimType = claimType;
        this.logicalKey = logicalKey;
        this.claimReason = claimReason;
        this.userId = userId;
        this.userName = userName;
    }
}
