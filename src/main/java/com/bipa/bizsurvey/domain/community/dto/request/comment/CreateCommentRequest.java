package com.bipa.bizsurvey.domain.community.dto.request.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateCommentRequest {

    //

    @NotBlank(message = "댓글 내용을 입력하셔야 합니다.")
    private String content;

}
