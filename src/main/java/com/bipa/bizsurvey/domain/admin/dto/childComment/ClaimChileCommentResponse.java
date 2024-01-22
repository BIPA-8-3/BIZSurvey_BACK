package com.bipa.bizsurvey.domain.admin.dto.childComment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimChileCommentResponse {
    private Long childCommentId;
    private String content;
    private String nickName;
    private String createTime;
    private Long userId;
}



