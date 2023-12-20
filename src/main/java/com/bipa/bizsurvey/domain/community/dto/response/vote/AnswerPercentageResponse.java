package com.bipa.bizsurvey.domain.community.dto.response.vote;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerPercentageResponse {

    //
    private Long voteAnswerId;
    private String name;
    private long value;
}
