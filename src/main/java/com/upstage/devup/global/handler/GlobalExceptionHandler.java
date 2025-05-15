package com.upstage.devup.global.handler;

import com.upstage.devup.auth.exception.InvalidLoginException;
import com.upstage.devup.global.domain.dto.ErrorResponse;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.global.exception.ValueAlreadyInUseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLogin(InvalidLoginException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("INVALID_LOGIN", e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(ValueAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleValueAlreadInUse(ValueAlreadyInUseException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("VALUE_ALREADY_IN_USE", e.getMessage()));
    }
}
