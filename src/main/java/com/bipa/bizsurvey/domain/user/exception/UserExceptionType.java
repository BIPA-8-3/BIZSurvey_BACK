package com.bipa.bizsurvey.domain.user.exception;

import com.bipa.bizsurvey.global.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
public enum UserExceptionType implements BaseExceptionType {

    ALREADY_EXIST_EMAIL(600, HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NON_EXIST_EMAIL(600, HttpStatus.BAD_REQUEST, "존재하는 이메일이 없습니다."),
    ALREADY_EXIST_NICKNAME(600, HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    ALREADY_EXIST_AUTH_NUMBER(600, HttpStatus.BAD_REQUEST, "인증 번호를 확인해주세요."),
    NO_PERMISSION(600, HttpStatus.BAD_REQUEST, "권한이 없는 유저입니다."),
    NON_EXIST_USER(600, HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    JWT_VERIFICATION(401, HttpStatus.BAD_REQUEST, "유효하지 않은 Refresh Token 입니다."),
    NON_AUTH_PASSWORDEMAIL(600, HttpStatus.BAD_REQUEST, "비밀번호 재설정 링크가 만료되었습니다."),

    KAKAO_PROVIDER_CHECK(600, HttpStatus.BAD_REQUEST, "카카오로 가입한 계정입니다. 비밀번호 찾기는 이메일로 가입한 경우에만 가능합니다"),
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
