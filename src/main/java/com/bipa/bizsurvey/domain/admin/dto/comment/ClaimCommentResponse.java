package com.bipa.bizsurvey.domain.admin.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimCommentResponse {
    private Long commentId;
    private String content;
    private String nickName;
    private String createTime;
    private Long userId;
}
