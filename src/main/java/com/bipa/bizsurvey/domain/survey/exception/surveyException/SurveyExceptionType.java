package com.bipa.bizsurvey.domain.survey.exception.surveyException;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum SurveyExceptionType implements BaseExceptionType {

    NON_EXIST_SURVEY(404, HttpStatus.NOT_FOUND, "존재하지 않는 설문지입니다."),
    ALREADY_DELETED(404, HttpStatus.NOT_FOUND, "이미 삭제된 설문지입니다."),
    NO_PERMISSION(404, HttpStatus.NOT_FOUND, "접근 권한이 없습니다."),
    MISSING_REQUIRED_VALUE(404, HttpStatus.BAD_REQUEST, "필수 값이 누락되었습니다."),
    QUESTION_LIMIT_EXCEEDED(400, HttpStatus.BAD_REQUEST, "설문의 최대 허용 질문 수를 초과했습니다."),
    ALREADY_PARTICIPATED(404, HttpStatus.BAD_REQUEST, "이미 참여한 회원입니다.")

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
