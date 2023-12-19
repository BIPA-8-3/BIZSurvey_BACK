package com.bipa.bizsurvey.domain.community.exception.childCommentException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ChildCommentExceptionType implements BaseExceptionType {
    //
    NON_EXIST_CHILD_COMMENT(600, HttpStatus.BAD_REQUEST, "존재하지 않는 대댓글입니다."),
    ALREADY_DELETED(600, HttpStatus.BAD_REQUEST, "이미 삭제된 대댓글입니다.")
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