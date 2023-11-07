package com.bipa.bizsurvey.domain.post.postException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
public enum PostExceptionType implements BaseExceptionType {

    ALREADY_EXIST_POST(600, HttpStatus.BAD_REQUEST, "이미 존재하는 게시물입니다.")
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
