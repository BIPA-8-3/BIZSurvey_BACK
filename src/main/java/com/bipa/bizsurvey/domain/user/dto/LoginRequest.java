package com.bipa.bizsurvey.domain.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
@Data
public class LoginRequest {
    @Email(message = "이메일 형식으로 작성해주세요.")
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
