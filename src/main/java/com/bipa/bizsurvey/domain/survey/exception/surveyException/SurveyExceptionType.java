package com.bipa.bizsurvey.domain.survey.exception.surveyException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum SurveyExceptionType implements BaseExceptionType {

    NOT_EXIST_SURVEY(404, HttpStatus.NOT_FOUND, "존재하지 않는 설문지입니다.");

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
