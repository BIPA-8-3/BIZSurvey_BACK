package com.bipa.bizsurvey.domain.survey.dto;


import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionInWorkspaceResponse {

    private Long id;

    private String surveyQuestion;

    private AnswerType answerType;

    private int score;

    private List<AnswerInWorkspaceResponse> answers;


}
