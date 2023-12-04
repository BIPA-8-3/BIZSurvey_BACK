package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuestionResponse {

    private Long questionId;

    private String surveyQuestion;

    private String answerType;

    private int score;

    private int step;

    private Boolean isRequired;

    private List<AnswerResponse> answers;


}
