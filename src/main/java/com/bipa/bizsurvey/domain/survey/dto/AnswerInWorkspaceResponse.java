package com.bipa.bizsurvey.domain.survey.dto;

import com.bipa.bizsurvey.domain.survey.enums.Correct;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnswerInWorkspaceResponse {

    private Long id;

    private String surveyAnswer;

    private Correct correct;

}
