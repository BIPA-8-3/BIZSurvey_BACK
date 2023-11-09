package com.bipa.bizsurvey.domain.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class EmailCheckRequest {
    @Email
    private String email;
}
