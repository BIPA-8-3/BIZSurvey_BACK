package com.bipa.bizsurvey.domain.survey.dto;


import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class SurveyInWorkspaceResponse {

    private Long id;

    private String title;

    private String content;

    private SurveyType type;

    private List<QuestionInWorkspaceResponse> questions;


}
