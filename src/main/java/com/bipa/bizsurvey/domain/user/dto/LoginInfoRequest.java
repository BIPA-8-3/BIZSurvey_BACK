package com.bipa.bizsurvey.domain.user.dto;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginInfoRequest {
    private long id;
    private String password;
    private String email;
    private String nickname;
    private Plan planSubscribe;
    private String name;
    private Gender gender;
    private String provider;

    public LoginInfoRequest(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.planSubscribe = user.getPlanSubscribe();
        this.name = user.getName();
        this.gender = user.getGender();
        this.provider = user.getProvider();
    }

    public User toEntity(){
        return User.builder()
                .id(id)
                .email(email)
                .name(name)
                .nickname(nickname)
                .gender(gender)
                .planSubscribe(Plan.COMMUNITY)
                .provider(provider)
                .build();
    }
}
