package com.bipa.bizsurvey.domain.survey.dto;

import com.bipa.bizsurvey.domain.survey.enums.Correct;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerInWorkspaceResponse {

    private Long id;

    private String surveyAnswer;

    private Correct correct;

}
