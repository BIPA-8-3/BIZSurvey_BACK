package com.bipa.bizsurvey.domain.community.dto.response.surveyPost;

import com.bipa.bizsurvey.domain.community.dto.response.comment.CommentResponse;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
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

}
