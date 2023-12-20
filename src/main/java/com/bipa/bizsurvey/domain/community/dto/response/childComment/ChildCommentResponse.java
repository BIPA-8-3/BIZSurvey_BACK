package com.bipa.bizsurvey.domain.community.dto.response.childComment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChildCommentResponse {

    //


    private Long childCommentId;
    private String content;
    private String nickName;
    private String createTime;

    // 유저 이미지 URL
    private String thumbImageUrl;

}
