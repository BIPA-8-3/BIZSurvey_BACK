package com.bipa.bizsurvey.domain.survey.dto.survey;


import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyInWorkspaceResponse {

    private Long surveyId;

    private String title;

    private String content;

    private SurveyType surveyType;

    private List<QuestionInWorkspaceResponse> questions;


}
