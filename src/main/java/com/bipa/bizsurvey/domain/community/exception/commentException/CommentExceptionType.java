package com.bipa.bizsurvey.domain.community.exception.commentException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum CommentExceptionType implements BaseExceptionType {
    NON_EXIST_COMMENT(600, HttpStatus.BAD_REQUEST, "존재하지 않는 댓글입니다."),
    ALREADY_DELETED(600, HttpStatus.BAD_REQUEST, "이미 삭제된 댓글입니다.")
    ;


    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
