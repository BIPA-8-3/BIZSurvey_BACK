package com.bipa.bizsurvey.domain.user.dto.mypage;

import com.bipa.bizsurvey.domain.user.domain.Claim;
import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserClaimResponse {
    private Long userId;
    private ClaimType claimType;
    private ClaimReason claimReason;
    private String regDate;

    public UserClaimResponse(Claim claim) {
        this.userId = claim.getUser().getId();
        this.claimType = claim.getClaimType();
        this.claimReason = claim.getClaimReason();
        this.regDate = String.valueOf(claim.getRegDate());
    }
}
