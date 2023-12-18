package com.bipa.bizsurvey.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MailAuthRequest {
    @Email
    private String email;

    @NotBlank
    private String authNumber;
}
