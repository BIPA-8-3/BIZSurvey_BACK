package com.bipa.bizsurvey.domain.user.dto.claim;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClaimListResponse {

    private List<String> claimList;
}
