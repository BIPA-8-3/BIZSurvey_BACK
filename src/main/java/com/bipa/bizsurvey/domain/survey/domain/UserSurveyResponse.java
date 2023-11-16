package com.bipa.bizsurvey.domain.survey.domain;

import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.survey.dto.request.ParticipateSurveyRequest;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.user.domain.User;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_survey_response")
public class UserSurveyResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_survey_response_id")
    private Long id;

    @Column(nullable = false)
    private String answer;

    private String url;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_post_id")
    private SurveyPost surveyPost;

    @Builder
    public UserSurveyResponse(String answer, AnswerType answerType, User user, Question question, SurveyPost surveyPost,
            String url) {
        this.answer = answer;
        this.url = url;
        this.answerType = answerType;
        this.user = user;
        this.question = question;
        this.surveyPost = surveyPost;
    }

    public static UserSurveyResponse toEntity(ParticipateSurveyRequest participateSurvey, User user, Question question,
            SurveyPost surveyPost, String answer) {
        return UserSurveyResponse.builder()
                .answer(answer)
                .answerType(participateSurvey.getAnswerType())
                .user(user)
                .question(question)
                .url(participateSurvey.getUrl())
                .surveyPost(surveyPost)
                .build();
    }

}
