package com.bipa.bizsurvey.domain.survey.dto;

import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {

    private String surveyQuestion;

    private AnswerType answerType;

    private int score;

    private List<?> answers;
}
