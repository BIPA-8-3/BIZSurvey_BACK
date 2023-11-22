package com.bipa.bizsurvey.domain.survey.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreAnswerResponse {

    private Long questionId;

    private String answer;
}
