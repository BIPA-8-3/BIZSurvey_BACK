package com.bipa.bizsurvey.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailAuthRequest {
    private String mail;
    private String authNumber;
}
