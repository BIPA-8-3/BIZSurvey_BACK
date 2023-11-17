package com.bipa.bizsurvey.domain.survey.dto.request;


import com.bipa.bizsurvey.domain.survey.enums.Correct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnswerRequest {

    @NotBlank(message = "옵션을 입력해주세요.")
    private String surveyAnswer;

    private Correct correct;

    private int step;

}
