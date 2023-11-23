package com.bipa.bizsurvey.domain.community.exception.voteException;

import com.bipa.bizsurvey.global.exception.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class VoteExceptionHandler {
    @ExceptionHandler(VoteException.class)
    public ResponseEntity<?> handleBaseEx(VoteException exception){
        ExceptionDto exceptionDto = new ExceptionDto(
                exception.getExceptionType().getErrorCode(),
                exception.getExceptionType().getHttpStatus(),
                exception.getExceptionType().getErrorMessage()
        );

        return ResponseEntity.badRequest().body(exceptionDto);
    }
}
