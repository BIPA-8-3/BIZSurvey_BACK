package com.bipa.bizsurvey.domain.survey.enums;


import lombok.Getter;

@Getter
public enum SurveyType {

    NORMAL("기본"),
    SCORE("점수");

    private final String value;


    SurveyType(String value) {
        this.value = value;
    }
}
