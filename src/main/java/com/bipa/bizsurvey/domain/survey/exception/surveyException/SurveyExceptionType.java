package com.bipa.bizsurvey.domain.survey.exception.surveyException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum SurveyExceptionType implements BaseExceptionType {

    NOT_EXIST_SURVEY(404, HttpStatus.NOT_FOUND, "존재하지 않는 설문지입니다."),
    ALREADY_DELETED(404, HttpStatus.NOT_FOUND, "이미 삭제된 설문지입니다."),
    NO_PERMISSION(404, HttpStatus.NOT_FOUND, "접근 권한이 없습니다.")
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
