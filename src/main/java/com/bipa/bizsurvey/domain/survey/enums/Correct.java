package com.bipa.bizsurvey.domain.survey.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Correct {

    //

    YES("정답"),
    NO("오답");


    private final String value;


    Correct(String value) {
        this.value = value;
    }

//    @JsonCreator
//    public static Correct from(String type){
//        for (Correct correct : Correct.values()) {
//            if (correct.getValue().equals(type)){
//                return correct;
//            }
//        }
//        return null;
//    }
}
