package com.bipa.bizsurvey.domain.community.dto.request.post;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreatePostRequest {

    @NotBlank(message = "게시물 제목은 필수 입력값입니다.")
    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String title;

    @NotBlank(message = "게시물 내용을 입력하셔야 합니다.")
    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String content;

    private List<String> imageUrlList;

}
