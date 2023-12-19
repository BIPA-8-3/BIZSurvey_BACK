package com.bipa.bizsurvey.domain.admin.dto.claim;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimDetailResponse {
    private Long id;
    private String claimType;
    private Long logicalKey;
    private String claimReason;
    private Long userid;
    private String nickname;
    private String email;
    private String createTime;
}
