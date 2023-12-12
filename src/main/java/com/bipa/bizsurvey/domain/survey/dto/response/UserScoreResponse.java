package com.bipa.bizsurvey.domain.survey.dto.response;


import com.bipa.bizsurvey.domain.survey.enums.Correct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserScoreResponse {

    private Long questionId;

    private int score;

    private List<String> userAnswer;

}

