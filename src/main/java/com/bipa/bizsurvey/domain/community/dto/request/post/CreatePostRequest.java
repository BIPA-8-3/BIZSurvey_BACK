package com.bipa.bizsurvey.domain.community.dto.request.post;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreatePostRequest {

    //

    @NotBlank(message = "게시물 제목은 필수 입력값입니다.")
    @Size(min = 2, message = "최소 두 글자 이상 입력하셔야 합니다.")
    private String title;

    private String content;

    private Long voteId; // 투표 생성 시 저장되어야 하는 값.

    private List<String> imageUrlList;

}
