package com.bipa.bizsurvey.domain.survey.dto.survey;

import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {

    @NotBlank(message = "질문을 입력해주세요.")
    private String surveyQuestion;

    private AnswerType answerType;

    private int score;

    private List<CreateAnswerRequest> answers;
}
