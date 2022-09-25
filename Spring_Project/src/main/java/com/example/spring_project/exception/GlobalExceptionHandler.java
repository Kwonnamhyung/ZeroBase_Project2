package com.example.spring_project.exception;

import com.example.spring_project.dto.ErrorResponse;
import com.example.spring_project.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e) {
        log.error("{} is occurred.",e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(),e.getErrorMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException is occurred" , e);

        return new ErrorResponse(ErrorCode.INVALID_ID_REQUEST,ErrorCode.INVALID_ID_REQUEST.getDescription());
    }


    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception is occurred.",e);

        return new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR,ErrorCode.INTERNAL_SERVER_ERROR.getDescription());
    }
}
