package com.bipa.bizsurvey.domain.survey.dto.response;

import com.bipa.bizsurvey.domain.survey.enums.Correct;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponse {

    //

    private Long answerId;

    private String surveyAnswer;

    private int step;

    private Correct correct;


}
