package com.bipa.bizsurvey.domain.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class EmailCheckRequest {
    @Email(message = "이메일 형식으로 작성해주세요")
    private String email;
}
