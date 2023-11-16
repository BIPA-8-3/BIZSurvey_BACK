package com.bipa.bizsurvey.domain.user.exception;

import com.bipa.bizsurvey.global.exception.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleBaseEx(UserException exception){
        ExceptionDto exceptionDto = new ExceptionDto(
                exception.getExceptionType().getErrorCode(),
                exception.getExceptionType().getHttpStatus(),
                exception.getExceptionType().getErrorMessage()
        );

        return ResponseEntity.badRequest().body(exceptionDto);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<?> AuthenticationFailureHandler(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        List<ExceptionDto> errorDtoList = new ArrayList<>();
        for(FieldError fieldError : bindingResult.getFieldErrors()){
            ExceptionDto errorDto = new ExceptionDto(400, HttpStatus.BAD_REQUEST, fieldError.getField()+" : "+ fieldError.getDefaultMessage());
            errorDtoList.add(errorDto);
        }
        return ResponseEntity.badRequest().body(errorDtoList);
    }
}
