package com.bipa.bizsurvey.domain.survey.dto;


import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSurveyRequest {

    @NotBlank(message = "설문지 제목은 필수 입력값입니다.")
    private String title;

    @NotBlank
    private String content;


    private SurveyType surveyType;

    private List<?> questions;

}
