package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultResponse {

    private Long questionId;

    private String title;

    private AnswerType answerType;

    private List<AnswerResultResponse> answers;

}
