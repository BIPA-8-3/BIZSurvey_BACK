package com.bipa.bizsurvey.domain.community.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum CreateType {

    TODAY("new"),
    BEFORE("before")
    ;


    private final String value;
}
