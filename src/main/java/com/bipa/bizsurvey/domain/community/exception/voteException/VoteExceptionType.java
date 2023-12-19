package com.bipa.bizsurvey.domain.community.exception.voteException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum VoteExceptionType implements BaseExceptionType {
    //

    MAX_COUNT(400, HttpStatus.BAD_REQUEST, "투표의 선택란은 최대 5개까지 입력가능합니다."),
    NON_EXIST_VOTE(400, HttpStatus.BAD_REQUEST, "존재하지 않는 투표입니다."),
    NON_EXIST_ANSWER(400, HttpStatus.BAD_REQUEST, "존재하지 않는 선택란입니다."),
    ALREADY_CHECK(400, HttpStatus.BAD_REQUEST, "이미 투표를 하셨습니다.")

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
