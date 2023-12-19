package com.bipa.bizsurvey.domain.survey.dto.response;

import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalResultResponse {

    //
    private Long questionId;

    private String answer;

    private String url;

//    private AnswerType questionType;

    private AnswerType answerType;

}
