package com.bipa.bizsurvey.domain.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NicknameCheckRequest {
    @NotBlank
    private String nickname;
}
