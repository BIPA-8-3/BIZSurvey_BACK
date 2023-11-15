package com.bipa.bizsurvey.domain.user.dto;

import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.user.enums.Gender;
import com.bipa.bizsurvey.domain.user.enums.Plan;
import com.bipa.bizsurvey.global.config.oauth.OAuth2UserInfo;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequest {
    private long id;
    private String password;
    private String email;
    private String nickname;
    private Plan planSubscribe;
    private String name;
    private Gender gender;

    //CustomOAuth2UserService (OAuth2UserInfo -> LoginRequest)
    @Builder
    public LoginRequest(Long id, String email, String nickname, String password, Plan planSubscribe, String name, Gender gender) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.planSubscribe = planSubscribe;
        this.name = name;
        this.gender = gender;
    }

    public LoginRequest(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.planSubscribe = user.getPlanSubscribe();
        this.name = user.getName();
        this.gender = user.getGender();
    }


    public User toEntity(){
        return User.builder()
                .id(id)
                .email(email)
                .name(name)
                .nickname(nickname)
                .gender(gender)
                .planSubscribe(Plan.COMMUNITY)
                .build();
    }






}
