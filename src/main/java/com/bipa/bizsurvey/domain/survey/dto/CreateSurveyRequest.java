package com.bipa.bizsurvey.domain.survey.dto;


import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSurveyRequest {

//    @NotBlank(message = "설문지 제목은 필수 입력값입니다.")
//    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String title;

//    @NotBlank
//    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String content;

    private SurveyType surveyType;

    private Long workspaceId;

    private List<CreateQuestionRequest> questions;

}
