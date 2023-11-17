package com.bipa.bizsurvey.domain.survey.domain;


import com.bipa.bizsurvey.domain.survey.domain.Question;
import com.bipa.bizsurvey.domain.community.domain.SurveyPost;
import com.bipa.bizsurvey.domain.survey.dto.request.ParticipateSurvey;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_survey_response")
public class UserSurveyResponse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_survey_response_id")
    private Long id;

    @Column(nullable = false)
    private String answer;

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
    public UserSurveyResponse(String answer, AnswerType answerType, User user, Question question, SurveyPost surveyPost) {
        this.answer = answer;
        this.answerType = answerType;
        this.user = user;
        this.question = question;
        this.surveyPost = surveyPost;
    }

    public static UserSurveyResponse toEntity(ParticipateSurvey participateSurvey, User user,  Question question, SurveyPost surveyPost){
        return UserSurveyResponse.builder()
                .answer(participateSurvey.getAnswer())
                .answerType(participateSurvey.getAnswerType())
                .user(user)
                .question(question)
                .surveyPost(surveyPost)
                .build();
    }

}
