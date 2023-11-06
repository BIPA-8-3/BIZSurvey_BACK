package com.bipa.bizsurvey.global.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ErrorDto {

    private HttpStatus httpStatus;
    private String message;
    private List<String> errors;
    private String errorNum;


    public ErrorDto(HttpStatus httpStatus, String message, List<String> errors, String errorNum) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.errors = errors;
        this.errorNum = errorNum;
    }

    public ErrorDto(HttpStatus httpStatus, String message, String error, String errorNum) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.errors = List.of(error);
        this.errorNum = errorNum;
    }

    public static ErrorDto toErrorDto(ErrorCode errorCode){
        return new ErrorDto(
                errorCode.getHttpStatus(),
                errorCode.name(),
                errorCode.getDetail(),
                errorCode.getErrorNum()
        );
    }
}
