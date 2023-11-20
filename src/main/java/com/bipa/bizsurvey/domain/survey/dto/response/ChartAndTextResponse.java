package com.bipa.bizsurvey.domain.survey.dto.response;

import com.bipa.bizsurvey.domain.survey.enums.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartAndTextResponse {

    private Long questionId;

    private AnswerType questionType;

    private List<ChartAndTextResult> answers;


}
