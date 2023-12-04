package com.bipa.bizsurvey.domain.workspace.domain;


import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "shared_survey_response")
public class SharedSurveyResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shared_survey_response_id")
    private Long id;

    private String surveyAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_survey_id")
    private SharedSurvey sharedSurvey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_list_id")
    private SharedList sharedList;

    private String url;
    private String fileName;

    @Builder
    public SharedSurveyResponse(String surveyAnswer,
                                SharedSurvey sharedSurvey,
                                Question question,
                                SharedList sharedList,
                                String url,
                                String fileName) {
        this.surveyAnswer = surveyAnswer;
        this.sharedSurvey = sharedSurvey;
        this.question = question;
        this.sharedList = sharedList;
        this.url = url;
        this.fileName = fileName;
    }
}
