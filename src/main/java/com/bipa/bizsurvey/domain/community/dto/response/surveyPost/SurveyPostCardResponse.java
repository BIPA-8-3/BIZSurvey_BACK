package com.bipa.bizsurvey.domain.community.dto.response.surveyPost;

import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostImageResponse;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class SurveyPostCardResponse {

    //
    private Long postId;
    private Long surveyPostId;
    private String title;
    private String content;
    private int count;// 컨트롤러 리턴
    private String nickname;
    private int commentSize; // 컨트롤러 리턴
    private int participateCount; // 컨트롤러 리턴
    private String canAccess; // 컨트롤러 리턴
    private String thumbImageUrl;
    private String profile; // 유저 섬네일 이미지

    @Builder
    public SurveyPostCardResponse(Long postId, Long surveyPostId, String title, String content, int count, String nickname,
                                  int commentSize, int participateCount, String canAccess, String thumbImageUrl,
                                  String profile) {
        this.postId = postId;
        this.surveyPostId = surveyPostId;
        this.title = title;
        this.content = content;
        this.count = count;
        this.nickname = nickname;
        this.commentSize = commentSize;
        this.participateCount = participateCount;
        this.canAccess = canAccess;
        this.thumbImageUrl = thumbImageUrl;
        this.profile = profile;
    }
}
