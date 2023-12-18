package com.bipa.bizsurvey.domain.admin.dto.user;


import lombok.Data;

@Data
public class UserSearchRequest {
    private String email;
    private String name;
    private String nickname;
}
