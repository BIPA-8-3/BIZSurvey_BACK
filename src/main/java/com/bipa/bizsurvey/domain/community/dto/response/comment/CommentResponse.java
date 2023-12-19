package com.bipa.bizsurvey.domain.community.dto.response.comment;

import com.bipa.bizsurvey.domain.community.dto.response.childComment.ChildCommentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommentResponse {

    //

    private Long commentId;
    private String content;
    private String nickName;
    private String createTime;

    // 대댓글 리스트 추가
    private List<ChildCommentResponse> childCommentResponses;

}
