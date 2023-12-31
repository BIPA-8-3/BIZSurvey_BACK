package com.bipa.bizsurvey.domain.survey.domain;


import com.bipa.bizsurvey.domain.survey.dto.request.CreateAnswerRequest;
import com.bipa.bizsurvey.domain.survey.enums.Correct;
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
@Table(name = "answer")
public class Answer extends BaseEntity {


    //

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    private String surveyAnswer;

    @Enumerated(EnumType.STRING)
    private Correct correct;

    private int step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Question question;


    @Builder
    public Answer(String surveyAnswer, Correct correct, Question question, int step) {
        this.surveyAnswer = surveyAnswer;
        this.correct = correct;
        this.question = question;
        this.step = step;
    }

    public static Answer toEntity(CreateAnswerRequest createAnswerRequest, Question question) {
        return Answer.builder()
                .surveyAnswer(createAnswerRequest.getSurveyAnswer())
                .correct(createAnswerRequest.getCorrect())
                .question(question)
                .step(createAnswerRequest.getStep())
                .build();
    }
}
