package com.bipa.bizsurvey.domain.survey.dto.request;

import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSurveyRequest {

    //

    private Long surveyId;

    @NotBlank(message = "설문지 제목은 필수 입력값입니다.")
    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String content;

    private SurveyType surveyType;

    private List<UpdateQuestionRequest> updateQuestions;
    private List<CreateQuestionRequest> createQuestions;


}
