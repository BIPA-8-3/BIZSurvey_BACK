package com.bipa.bizsurvey.domain.community.exception.childCommentException;

import com.bipa.bizsurvey.domain.community.exception.commentException.CommentException;
import com.bipa.bizsurvey.domain.community.exception.postException.PostException;
import com.bipa.bizsurvey.global.exception.ExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ChildCommentExceptionHandler {
    @ExceptionHandler(ChildCommentException.class)
    public ResponseEntity<?> handleBaseEx(PostException exception){
        ExceptionDto exceptionDto = new ExceptionDto(
                exception.getExceptionType().getErrorCode(),
                exception.getExceptionType().getHttpStatus(),
                exception.getExceptionType().getErrorMessage()
        );

        return ResponseEntity.badRequest().body(exceptionDto);
    }
}
