package com.bipa.bizsurvey.domain.user.dto.claim;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimReasonResponse {
    private String claimReason;
}
