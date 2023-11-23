package com.bipa.bizsurvey.domain.survey.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyListResponse {

    private Long surveyId;

    private String title;

}
