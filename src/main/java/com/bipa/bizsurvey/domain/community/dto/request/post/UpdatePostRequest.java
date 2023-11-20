package com.bipa.bizsurvey.domain.community.dto.request.post;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
public class UpdatePostRequest {
    @NotBlank(message = "수정할 제목을 입력하셔야 합니다.")
    private String title;

    @NotBlank(message = "수정할 내용을 입력하셔야 합니다.")
    private String content;

    private List<String> addImgUrlList;

    private List<String> deleteImgUrlList;

}
