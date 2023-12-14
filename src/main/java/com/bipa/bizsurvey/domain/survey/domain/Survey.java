package com.bipa.bizsurvey.domain.survey.domain;


import com.bipa.bizsurvey.domain.survey.dto.request.CreateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.dto.request.UpdateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.enums.SurveyType;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.domain.workspace.domain.Workspace;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "survey")
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;

    @Column(nullable = false)
    private String title;


    private String content;

    @Enumerated(EnumType.STRING)
    private SurveyType surveyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Survey(String title, String content, SurveyType surveyType, Workspace workspace, User user) {
        this.title = title;
        this.content = content;
        this.surveyType = surveyType;
        this.workspace = workspace;
        this.user = user;
    }

    public static Survey toEntity(User user, Workspace workspace, CreateSurveyRequest createSurveyRequest){
        return Survey.builder()
                .title(createSurveyRequest.getTitle())
                .content(createSurveyRequest.getContent())
                .surveyType(createSurveyRequest.getSurveyType())
                .user(user)
                .workspace(workspace)
                .build();

    }

    public void updateSurvey(UpdateSurveyRequest updateSurveyRequest){
        this.title = updateSurveyRequest.getTitle();
        this.content = updateSurveyRequest.getContent();
        this.surveyType = updateSurveyRequest.getSurveyType();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

}
