package com.bipa.bizsurvey.domain.workspace.enums;

import lombok.Getter;

@Getter
public enum WorkspaceType {


    PERSONAL("개인 워크스페이스"),
    COMPANY("기업 워크스페이스");


    private final String value;

    WorkspaceType(String value){
        this.value = value;
    }
}
