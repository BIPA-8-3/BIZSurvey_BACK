package com.bipa.bizsurvey.domain.community.dto.request.surveyPost;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateSurveyPostRequest {

    @NotBlank(message = "게시물 제목은 필수 입력값입니다.")
    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String title;

    @NotBlank(message = "게시물 내용을 입력하셔야 합니다.")
    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String content;

    // surveyPost

    @NotNull(message = "설문지의 시작일을 입력하셔야 합니다.")
    private LocalDateTime startDateTime;

    @NotNull(message = "설문지의 종료일을 입력하셔야 합니다.")
    private LocalDateTime endDateTime;


    @NotNull(message = "설문지를 지정해줘야 합니다.")
    private Long surveyId;

    private String thumbImageUrl;

    private List<String> imageUrlList;
}
