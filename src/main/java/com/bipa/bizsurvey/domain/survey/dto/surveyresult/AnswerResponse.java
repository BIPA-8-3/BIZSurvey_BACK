package com.bipa.bizsurvey.domain.survey.dto.surveyresult;


import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnswerResponse {

    private Long questionId;

    private String surveyQuestion;

    private AnswerType answerType;

    private String answer;

    private int score;

}
