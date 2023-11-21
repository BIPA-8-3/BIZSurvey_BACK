package com.bipa.bizsurvey.domain.admin.dto.claim;

import com.bipa.bizsurvey.domain.user.enums.ClaimReason;
import com.bipa.bizsurvey.domain.user.enums.ClaimType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimDetailResponse {
    private Long id;
    private String claimType;
    private Long logicalKey;
    private String claimReason;
}
