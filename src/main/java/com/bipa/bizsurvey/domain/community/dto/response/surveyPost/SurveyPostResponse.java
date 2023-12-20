package com.bipa.bizsurvey.domain.community.dto.response.surveyPost;

import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import com.bipa.bizsurvey.domain.community.dto.response.post.PostImageResponse;
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

    //
    private Long postId;
    private String title;
    private String content;
    private int count;
    private String nickname;
    private String createDate;
    private String startDateTime;
    private String endDateTime;
    private List<CommentResponse> commentList;
    private int commentSize;
    private List<PostImageResponse> imageResponseList;
    private String thumbImageUrl;
    private int participateCount;
    private String canAccess;
    private int reported; // 0 ->신고 X, 1 -> 신고 O
    private Long surveyId; // 설문 ID 리턴
    private String profile;


    @Builder
    public SurveyPostResponse(Long postId, String title, String content, int count, String nickname, String createDate,
                              String startDateTime, String endDateTime, List<CommentResponse> commentList,
                              int commentSize, List<PostImageResponse> imageResponseList, int participateCount, String canAccess,
                              int reported, Long surveyId, String thumbImageUrl, String profile) {


        this.postId = postId;
        this.title = title;
        this.content = content;
        this.count = count;
        this.nickname = nickname;
        this.createDate = createDate;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.commentList = commentList;
        this.commentSize = commentSize;
        this.imageResponseList = imageResponseList;
        this.thumbImageUrl = thumbImageUrl;
        this.participateCount = participateCount;
        this.canAccess = canAccess;
        this.reported = reported;
        this.surveyId = surveyId;
        this.profile = profile;
    }
}
