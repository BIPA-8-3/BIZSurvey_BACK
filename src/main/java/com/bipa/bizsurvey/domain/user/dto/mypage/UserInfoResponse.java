package com.bipa.bizsurvey.domain.user.dto.mypage;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class UserInfoResponse {
    private Long id;
    private String email;
    private String nickname;
    private Plan planSubscribe;
    private String name;
    private Gender gender;
    private String birthdate;
    private String profile;
}
