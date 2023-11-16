package com.bipa.bizsurvey.domain.user.enums;

import lombok.Getter;

@Getter
public enum ClaimList {

    PROMOTION("스팸홍보/도배글"),
    ILLEGAL_INFO("음란물/불법정보/불법촬영물"),
    ABUSIVE("욕설/혐오/차별적/불쾌한 표현"),
    DISCLOSURE("개인정보 노출"),
    COPYRIGHT("명예훼손/저작권 침해"),
    FALSE_REPORTER("허위 신고자");

    private final String value;

    ClaimList(String value) {
        this.value = value;
    }

}
