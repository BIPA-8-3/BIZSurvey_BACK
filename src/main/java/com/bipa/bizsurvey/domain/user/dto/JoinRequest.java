package com.bipa.bizsurvey.domain.user.dto;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class JoinRequest {
    @Pattern(regexp = "^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 작성해주세요")
    @NotBlank
    private String email;

    @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    private Gender gender;

    @NotBlank
    private String birthdate;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    @NotBlank
    private String password;

    private String company;

    private Plan planSubscribe;

    public User toEntity(BCryptPasswordEncoder passwordEncoder){
        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .gender(gender)
                .birthdate(birthdate)
                .password(passwordEncoder.encode(password))
                .company(company)
                .planSubscribe(Plan.COMMUNITY)
                .build();
    }
}
