package com.bipa.bizsurvey.domain.user.enums;


import lombok.Getter;

@Getter
public enum Plan {
    COMMUNITY("커뮤니티"),
    NORMAL_SUBSCRIBE("일반 구독"),
    COMPANY_SUBSCRIBE("기업 구독");

    private final String value;


    Plan(String value) {
        this.value = value;
    }
}
