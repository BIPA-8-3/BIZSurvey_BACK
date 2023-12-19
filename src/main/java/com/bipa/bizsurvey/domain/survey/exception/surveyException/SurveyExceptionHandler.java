package com.bipa.bizsurvey.domain.survey.exception.surveyException;


import com.bipa.bizsurvey.global.exception.ExceptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SurveyExceptionHandler {

    //

    @ExceptionHandler(SurveyException.class)
    public ResponseEntity<?> handleBaseEx(SurveyException exception) {
        ExceptionDto exceptionDto = new ExceptionDto(
                exception.getExceptionType().getErrorCode(),
                exception.getExceptionType().getHttpStatus(),
                exception.getExceptionType().getErrorMessage()
        );
        return ResponseEntity.badRequest().body(exceptionDto);
    }

}
