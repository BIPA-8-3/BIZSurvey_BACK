package com.bipa.bizsurvey.domain.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static ClaimType from(String sub){
        for(ClaimType claimType : ClaimType.values()){
            if(claimType.getValue().equals(sub)){
                return claimType;
            }
        }
        return null;
    }

}
