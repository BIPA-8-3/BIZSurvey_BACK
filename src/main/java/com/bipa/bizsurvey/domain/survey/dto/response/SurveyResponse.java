package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SurveyResponse {

    private Long surveyId;

    private String title;

    private String content;

    private SurveyType surveyType;

    private List<QuestionResponse> questions;


}
