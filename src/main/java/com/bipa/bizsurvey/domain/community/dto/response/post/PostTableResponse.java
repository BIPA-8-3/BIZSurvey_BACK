package com.bipa.bizsurvey.domain.community.dto.response.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class PostTableResponse implements Serializable {
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

    @Builder
    public PostTableResponse(Long postId, String title, int count, String nickname, String createTime, int commentSize, Long voteId, String createType, String isBest) {
        this.postId = postId;
        this.title = title;
        this.count = count;
        this.nickname = nickname;
        this.createTime = createTime;
        this.commentSize = commentSize;
        this.voteId = voteId;
        this.createType = createType;
        this.isBest = isBest;
    }
}
