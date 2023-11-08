package com.bipa.bizsurvey.domain.survey.dto;


import com.bipa.bizsurvey.domain.survey.enums.Correct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCreateRequest {

    private String surveyAnswer;

    private Correct correct;

}
