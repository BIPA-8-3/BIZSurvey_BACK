package com.bipa.bizsurvey.domain.workspace.domain;

import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_list_id")
    private SharedList sharedList;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    @Column(nullable = false)
    private String surveyAnswer;

    private String url;

    @Builder
    public SharedSurveyResponse(String surveyAnswer,
            AnswerType answerType,
            Question question,
            SharedList sharedList,
            String url) {
        this.surveyAnswer = surveyAnswer;
        this.answerType = answerType;
        this.question = question;
        this.sharedList = sharedList;
        this.url = url;
    }
}
