package com.bipa.bizsurvey.domain.community.dto.request.vote.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteAnswerResponse {
    private Long voteAnswerId;
    private String answer;
}
