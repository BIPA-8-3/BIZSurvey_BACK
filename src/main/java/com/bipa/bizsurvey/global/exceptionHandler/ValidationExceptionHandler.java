package com.bipa.bizsurvey.global.exceptionHandler;


import com.bipa.bizsurvey.global.exception.ExceptionDto;
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
        List<ExceptionDto> errorDtoList = new ArrayList<>();
        for(FieldError fieldError : bindingResult.getFieldErrors()){
            ExceptionDto errorDto = new ExceptionDto(400, HttpStatus.BAD_REQUEST, fieldError.getField()+" : "+ fieldError.getDefaultMessage());
            errorDtoList.add(errorDto);
        }

        return ResponseEntity.ok().body(errorDtoList);
    }

}
