package com.bipa.bizsurvey.domain.community.dto.request.post;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SearchPostRequest {

    @NotBlank(message = "검색 키워드를 입력하셔야 합니다.")
    private String keyword;

}
