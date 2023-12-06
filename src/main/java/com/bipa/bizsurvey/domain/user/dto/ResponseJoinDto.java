package com.bipa.bizsurvey.domain.user.dto;

import com.bipa.bizsurvey.domain.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseJoinDto {
    private Long id;
    private String email;
    private String name;

    public ResponseJoinDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }
}