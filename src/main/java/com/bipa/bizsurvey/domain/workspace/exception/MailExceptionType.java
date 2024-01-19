package com.bipa.bizsurvey.domain.workspace.exception;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MailExceptionType implements BaseExceptionType {
    ENCRYPTION_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "암호화 중 오류가 발생했습니다."),
    MAIL_SENDING_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송 중 오류가 발생했습니다.");

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
