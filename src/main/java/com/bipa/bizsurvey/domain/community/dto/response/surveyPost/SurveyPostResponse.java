package com.bipa.bizsurvey.domain.community.dto.response.surveyPost;

import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
public class SurveyPostResponse {
    private Long postId;
    private String title;
    private String content;
    private int count;
    private String nickname;
    private String createDate;
    private int maxMember;
    private String startDateTime;
    private String endDateTime;
    private List<CommentResponse> commentList;
    private String canAccess;

    @Builder
    public SurveyPostResponse(Long postId, String title, String content, int count, String nickname, String createDate, int maxMember, String startDateTime, String endDateTime, List<CommentResponse> commentList, String canAccess) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.count = count;
        this.nickname = nickname;
        this.createDate = createDate;
        this.maxMember = maxMember;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.commentList = commentList;
        this.canAccess = canAccess;
    }
}
