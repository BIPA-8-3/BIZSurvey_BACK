package com.bipa.bizsurvey.domain.community.dto.response.post;

import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostTableResponse {
    private Long postId;
    private String title;
    private int count;
    private String nickname;
    private String createTime;
    // 달린 댓글 사이즈 리턴
    private int commentSize;

    // 투표 생성 여부 추가
    private Long voteId;

    //새롭게 생성된 게시물인지 아닌지 확인 여부
    private String createType;

    //조회수 기준 Best 게시물인지 아닌지
    private String isBest;
}
