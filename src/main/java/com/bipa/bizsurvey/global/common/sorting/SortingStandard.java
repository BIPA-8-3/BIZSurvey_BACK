package com.bipa.bizsurvey.global.common.sorting;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum SortingStandard {
    COUNT("조회수"),
    REG_DATE("최신순"),
    ;

    private final String value;

    SortingStandard(String value) {
        this.value = value;
    }

    @JsonCreator
    public static SortingStandard from(String sub){
        for(SortingStandard sortingStandard : SortingStandard.values()){
            if(sortingStandard.getValue().equals(sub)){
                 return sortingStandard;
            }
        }
        return null;
    }
}
