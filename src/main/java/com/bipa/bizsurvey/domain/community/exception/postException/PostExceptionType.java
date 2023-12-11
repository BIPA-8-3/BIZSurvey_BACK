package com.bipa.bizsurvey.domain.community.exception.postException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
public enum PostExceptionType implements BaseExceptionType {

    NON_EXIST_POST(600, HttpStatus.BAD_REQUEST, "존재하지 않는 게시물입니다."),
    ALREADY_DELETED(600, HttpStatus.BAD_REQUEST, "이미 삭제된 게시물입니다."),
    NO_RESULT(600, HttpStatus.BAD_REQUEST, "일치하는 결과물이 없습니다.")

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
