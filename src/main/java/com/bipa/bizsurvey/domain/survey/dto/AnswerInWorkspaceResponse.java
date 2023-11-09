package com.bipa.bizsurvey.domain.survey.dto;

import com.bipa.bizsurvey.domain.survey.enums.Correct;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerInWorkspaceResponse {

    private Long answerId;

    private String surveyAnswer;

    private Correct correct;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

}
