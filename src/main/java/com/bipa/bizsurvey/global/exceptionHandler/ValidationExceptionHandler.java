package com.bipa.bizsurvey.global.exceptionHandler;

import com.bipa.bizsurvey.global.error.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ValidationExceptionHandler {

    // Validation 모의로 400으로 지정
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        List<ErrorDto> errorDtoList = new ArrayList<>();
        for(FieldError fieldError : bindingResult.getFieldErrors()){
            ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, "VALIDATION_CHECK : " + fieldError.getField(), fieldError.getDefaultMessage(), "400");
            errorDtoList.add(errorDto);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDtoList);
    }

}
