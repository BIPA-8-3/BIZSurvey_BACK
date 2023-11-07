package com.bipa.bizsurvey.domain.post.postException;

import com.bipa.bizsurvey.global.exception.ExceptionDto;
import com.bipa.bizsurvey.domain.post.postException.PostException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class PostExceptionHandler {
    @ExceptionHandler(PostException.class)
    public ResponseEntity<?> handleBaseEx(PostException exception){
        ExceptionDto exceptionDto = new ExceptionDto(
                exception.getExceptionType().getErrorCode(),
                exception.getExceptionType().getHttpStatus(),
                exception.getExceptionType().getErrorMessage()
        );

        return ResponseEntity.ok().body(exceptionDto);
    }
}
