package com.bipa.bizsurvey.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 여기에 에러 코드 추가
    // ex) ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 ID입니다.", "405")
    ;

    private final HttpStatus httpStatus;
    private final String detail;
    private final String errorNum;
}
