package com.bipa.bizsurvey.domain.user.dto.mypage;

import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInfoUpdateRequest {
    private Long id;
    @NotBlank
    private String nickname;
    private String birthdate;
}
