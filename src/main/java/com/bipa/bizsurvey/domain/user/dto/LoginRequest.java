package com.bipa.bizsurvey.domain.user.dto;

import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private long id;
    private String password;
    private String email;
    private String nickname;
    private Plan planSubscribe;

    @Builder
    public LoginRequest(Long id, String email, String nickname, String password, Plan planSubscribe) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.planSubscribe = planSubscribe;
    }
}
