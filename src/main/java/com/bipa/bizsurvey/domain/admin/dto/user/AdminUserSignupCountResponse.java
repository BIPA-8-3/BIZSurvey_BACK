package com.bipa.bizsurvey.domain.admin.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminUserSignupCountResponse {
    private String dayOfWeek;
    private Long signupCount;


}
