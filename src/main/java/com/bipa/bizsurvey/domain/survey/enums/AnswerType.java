package com.bipa.bizsurvey.domain.survey.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum AnswerType {


    SINGLE_CHOICE("객관식(택1)"),
    MULTIPLE_CHOICE("객관식(복수형)"),
    TEXT("주관식"),
    FILE("파일"),
    CALENDAR("날짜");


    private final String value;

    AnswerType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AnswerType from(String type){
        for (AnswerType answerType : AnswerType.values()) {
            if (answerType.getValue().equals(type)){
                return answerType;
            }
        }
        return null;
    }
}
