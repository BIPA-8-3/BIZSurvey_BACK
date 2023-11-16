package com.bipa.bizsurvey.domain.user.dto.claim;

import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import lombok.Data;

@Data
public class ClaimRequest {
    private Long id;
    private ClaimReason claimReason;
    private ClaimType claimType;
}
