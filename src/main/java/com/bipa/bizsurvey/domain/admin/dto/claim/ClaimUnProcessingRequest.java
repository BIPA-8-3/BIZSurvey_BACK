package com.bipa.bizsurvey.domain.admin.dto.claim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimUnProcessingRequest {
    private Long claimId;
    private Long postId;
}
