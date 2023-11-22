package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.survey.enums.Correct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserScoreResponse {

    private Long questionId;

    private String userAnswer;

    private Correct correct;

    private int score;

}
