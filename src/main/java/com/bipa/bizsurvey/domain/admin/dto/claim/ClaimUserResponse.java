package com.bipa.bizsurvey.domain.admin.dto.claim;

import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimUserResponse {
    private Long id;
    private ClaimReason claimReason;
    private ClaimType claimType;
    private Long penalized;
    private String regdate;
}
