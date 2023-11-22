package com.bipa.bizsurvey.domain.community.dto.request.vote.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateVoteAnswerRequest {
    @NotBlank(message = "투표의 답변을 입력해주세요.")
    private String answer;
}
