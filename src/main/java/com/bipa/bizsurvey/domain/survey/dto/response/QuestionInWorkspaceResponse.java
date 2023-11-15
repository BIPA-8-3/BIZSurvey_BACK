package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuestionInWorkspaceResponse {

    private Long questionId;

    private String surveyQuestion;

    private AnswerType answerType;

    private int score;

    private List<AnswerInWorkspaceResponse> answers;


}
