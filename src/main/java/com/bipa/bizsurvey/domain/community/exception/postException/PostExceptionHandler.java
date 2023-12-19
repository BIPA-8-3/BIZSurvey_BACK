package com.bipa.bizsurvey.domain.community.exception.postException;

import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.global.exception.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class PostExceptionHandler {
    //
    @ExceptionHandler(PostException.class)
    public ResponseEntity<?> handleBaseEx(PostException exception){
        ExceptionDto exceptionDto = new ExceptionDto(
                exception.getExceptionType().getErrorCode(),
                exception.getExceptionType().getHttpStatus(),
                exception.getExceptionType().getErrorMessage()
        );

        return ResponseEntity.badRequest().body(exceptionDto);
    }
}
