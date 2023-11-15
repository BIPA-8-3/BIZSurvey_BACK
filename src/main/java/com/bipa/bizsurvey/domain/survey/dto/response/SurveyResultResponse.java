package com.bipa.bizsurvey.domain.survey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultResponse {

    List<String> answerUsers;

    List<AnswerResponse> answerResponses;

}
