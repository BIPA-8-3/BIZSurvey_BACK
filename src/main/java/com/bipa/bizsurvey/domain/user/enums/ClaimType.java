package com.bipa.bizsurvey.domain.user.enums;

import lombok.Getter;

@Getter
public enum ClaimType {
    POST("게시글"),
    COMMENT("댓글"),
    CHILD_COMMENT("대댓글");

    private final String value;


    ClaimType(String value) {
        this.value = value;
    }
}
