package com.bipa.bizsurvey.domain.survey.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ToString
public class SurveyInWorkspaceResponse {

    private Long id;

    private String title;

    private String content;

    private List<QuestionInWorkspaceResponse> questions;


}
