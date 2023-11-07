package com.bipa.bizsurvey.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ExceptionDto {

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
