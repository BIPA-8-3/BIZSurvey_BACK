package com.bipa.bizsurvey.domain.community.dto.response.post;

import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostResponse {

    //
    private Long postId;
    private String title;
    private String content;
    private int count;
    private String nickname;
    private String createTime;
    private List<CommentResponse> commentList;
    private List<PostImageResponse> imageResponseList;

    // 투표 생성 여부 추가
    private Long voteId;

    // 댓글 사이즈
    private int commentSize;

    // 신고당한 여부
    private int reported; // 0 ->신고 X, 1 -> 신고 O

    //게시물 작성 유저 섬네일 이미지
    private String thumbImageUrl;

}
