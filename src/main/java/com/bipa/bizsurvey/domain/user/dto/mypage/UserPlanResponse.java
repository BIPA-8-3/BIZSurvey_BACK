package com.bipa.bizsurvey.domain.user.dto.mypage;

import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPlanResponse {
    private Plan planSubscribe;
}
