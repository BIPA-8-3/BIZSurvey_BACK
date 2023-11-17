package com.bipa.bizsurvey.domain.survey.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SurveyListResponse {

    private Long surveyId;

    private String title;

}
