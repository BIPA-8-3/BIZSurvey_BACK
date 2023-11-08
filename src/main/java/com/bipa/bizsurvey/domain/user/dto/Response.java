package com.bipa.bizsurvey.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Response<T> {
    private final Integer code; // 1 성공, -1 실패
    private final String msg;
    private final T data;
}
