package com.bipa.bizsurvey.domain.user.enums;


import lombok.Getter;

@Getter
public enum Plan {
    COMMUNITY("커뮤니티"),
    NORMAL_SUBSCRIBE("개인 플랜"),
    COMPANY_SUBSCRIBE("그룹 플랜"),
    ADMIN("관리자");

    private final String value;


    Plan(String value) {
        this.value = value;
    }
}
