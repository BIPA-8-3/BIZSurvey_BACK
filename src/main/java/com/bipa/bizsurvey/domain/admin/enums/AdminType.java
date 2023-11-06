package com.bipa.bizsurvey.domain.admin.enums;

import lombok.Getter;

@Getter
public enum AdminType {

    INVITE("총 관리자"),
    INVITED("초대된 관리자");


    private final String value;

    AdminType(String value) {
        this.value = value;
    }
}
