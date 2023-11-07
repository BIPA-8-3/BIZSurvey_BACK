package com.bipa.bizsurvey.domain.user.exception;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
public enum UserExceptionType implements BaseExceptionType {

    ALREADY_EXIST_EMAIL(600, HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다")
    ;


    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
