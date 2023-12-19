package com.bipa.bizsurvey.domain.community.dto.response.vote;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
public class VoteResponse {

    //
    private String voteTitle;
    private List<VoteAnswerResponse> answerList;
    private String checked; // 이미 투표했는지 아닌지
}
