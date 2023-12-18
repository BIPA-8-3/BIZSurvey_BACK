package com.bipa.bizsurvey.domain.community.exception.surveyPostException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
public enum SurveyPostExceptionType implements BaseExceptionType {
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
