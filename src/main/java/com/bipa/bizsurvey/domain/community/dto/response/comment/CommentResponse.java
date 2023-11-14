package com.bipa.bizsurvey.domain.community.dto.response.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {

    private Long commentId;
    private String content;
    private String nickName;
    private String createTime;

}
