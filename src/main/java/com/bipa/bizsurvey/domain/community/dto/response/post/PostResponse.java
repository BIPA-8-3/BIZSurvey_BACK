package com.bipa.bizsurvey.domain.community.dto.response.post;

import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostResponse {
    private Long postId;
    private String title;
    private String content;
    private int count;
    private String nickname;
    private String createTime;
    private List<CommentResponse> commentList;
}