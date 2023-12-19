package com.bipa.bizsurvey.domain.survey.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResultResponse {

    //

    private Long questionId;

    private String title;

    private List<ScoreAnswerCount> answers;

}
