package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class AnswerResultResponse {

    private Long userAnswerId;

    private AnswerType answerType;

    // 객관식만
    private String answerTitle;
    // 객관식만 ..
    private int count;
    // 주관식, 날짜만
    private int userAnswer;

}


