package com.bipa.bizsurvey.domain.post.enums;

import lombok.Getter;

@Getter
public enum PostType {

    COMMUNITY("커뮤니티 게시글"),
    SURVEY("설문 게시글");


    private final String value;

    PostType(String value) {
        this.value = value;
    }
}
