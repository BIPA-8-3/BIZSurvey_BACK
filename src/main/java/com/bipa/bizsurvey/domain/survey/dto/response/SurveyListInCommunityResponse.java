package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.workspace.enums.WorkspaceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyListInCommunityResponse {

    private Long surveyId;

    private String title;

    private String workspaceName;

    private WorkspaceType workspaceType;
}
