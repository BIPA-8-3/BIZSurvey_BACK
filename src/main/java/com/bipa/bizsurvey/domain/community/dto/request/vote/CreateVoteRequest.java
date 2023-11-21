package com.bipa.bizsurvey.domain.community.dto.request.vote;

import com.bipa.bizsurvey.domain.community.dto.request.vote.CreateVoteAnswerRequest;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateVoteRequest {

    @NotBlank(message = "투표의 제목을 입력하셔야합니다.")
    private String voteQuestion;
    @NotNull(message = "투표의 항목을 입력해주세요!")
    private List<CreateVoteAnswerRequest> voteAnswer;
}
