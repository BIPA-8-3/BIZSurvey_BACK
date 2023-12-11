package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private Long questionId;

    private String surveyQuestion;

    private AnswerType answerType;

    private int score;

    private int step;

    private Boolean isRequired;

    private List<AnswerResponse> answers;

//    public QuestionResponse(Long questionId, String surveyQuestion, AnswerType answerType, int score , int step , Boolean isRequired, List<AnswerResponse> answers){
//        this.questionId = questionId;
//        this.surveyQuestion = surveyQuestion;
//        this.answerType = answerType.getValue();
//        this.step = step;
//        this.score = score;
//        this.isRequired = isRequired;
//        this.answers = answers;
//    }


}
