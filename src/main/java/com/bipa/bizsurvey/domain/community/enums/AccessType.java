package com.bipa.bizsurvey.domain.community.enums;

import lombok.Getter;

@Getter
public enum AccessType {

    CAN_NOT_START("대기"),
    CAN_START("참여 가능"),
    CLOSED("설문 종료")
    ;

    private final String isAccess;

    AccessType(String isAccess) {
        this.isAccess = isAccess;
    }
}
