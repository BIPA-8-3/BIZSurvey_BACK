package com.bipa.bizsurvey.domain.survey.enums;


import lombok.Getter;

@Getter
public enum AnswerType {


    SINGLE_CHOICE("객관식(택1)"),
    MULTIPLE_CHOICE("객관식(복수형)"),
    TEXT("주관식"),
    FILE("파일"),
    CALENDER("날짜");


    private final String value;

    AnswerType(String value) {
        this.value = value;
    }
}
