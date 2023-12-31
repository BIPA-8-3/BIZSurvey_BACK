package com.bipa.bizsurvey.domain.survey.dto.request;


import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipateSurveyRequest {

    //

    private Long questionId;

    private List<String> answer;

    private AnswerType answerType;

    private String url;

}
