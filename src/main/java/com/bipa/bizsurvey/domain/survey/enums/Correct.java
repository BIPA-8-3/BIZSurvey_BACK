package com.bipa.bizsurvey.domain.survey.enums;


import lombok.Getter;

@Getter
public enum Correct {

    YES("정답"),
    NO("오답");


    private final String value;


    Correct(String value) {
        this.value = value;
    }

}
