package com.bipa.bizsurvey.domain.user.dto.mypage;

import lombok.Data;

@Data
public class UserProfileRequest {
    private Long userId;
    private String profile;
}
