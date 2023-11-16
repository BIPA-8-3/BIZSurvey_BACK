package com.bipa.bizsurvey.domain.userSurveyResponse.domain;


import com.bipa.bizsurvey.domain.question.domain.Question;
import com.bipa.bizsurvey.domain.surveyPost.domain.SurveyPost;
import com.bipa.bizsurvey.domain.user.domain.User;
import lombok.AccessLevel;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_post_id")
    SurveyPost surveyPost;




}
