package com.bipa.bizsurvey.domain.community.dto.request.vote.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerPercentageResponse {
    private Long voteAnswerId;
    private String answer;
    private double percentage;
}
