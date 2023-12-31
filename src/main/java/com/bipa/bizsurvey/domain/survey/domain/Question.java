package com.bipa.bizsurvey.domain.survey.domain;


import com.bipa.bizsurvey.domain.survey.dto.request.CreateQuestionRequest;
import com.bipa.bizsurvey.domain.survey.dto.request.UpdateQuestionRequest;
import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import com.bipa.bizsurvey.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "question")
public class Question extends BaseEntity {

    //


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(nullable = false)
    private String surveyQuestion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnswerType answerType;

    @Column(nullable = false)
    private Boolean isRequired;

    private int score;

    @Column(nullable = false)
    private int step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Survey survey;




    @Builder
    public Question(String surveyQuestion, AnswerType answerType, int score, Survey survey, int step, Boolean isRequired) {
        this.surveyQuestion = surveyQuestion;
        this.answerType = answerType;
        this.score = score;
        this.survey = survey;
        this.step = step;
        this.isRequired = isRequired;
    }

    public static Question toEntity(CreateQuestionRequest createQuestionRequest, Survey survey) {
        return Question.builder()
                .surveyQuestion(createQuestionRequest.getSurveyQuestion())
                .answerType(createQuestionRequest.getAnswerType())
                .score(createQuestionRequest.getScore())
                .survey(survey)
                .step(createQuestionRequest.getStep())
                .isRequired(createQuestionRequest.getIsRequired())
                .build();
    }

    public void updateQuestion(UpdateQuestionRequest updateQuestionRequest){
        this.surveyQuestion = updateQuestionRequest.getSurveyQuestion();
        this.answerType = updateQuestionRequest.getAnswerType();
        this.isRequired = updateQuestionRequest.getIsRequired();
        this.score = updateQuestionRequest.getScore();
        this.step = updateQuestionRequest.getStep();

    }
}
