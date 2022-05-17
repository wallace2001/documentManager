package com.rest.documentManager.exception;

import com.rest.documentManager.services.exceptions.ErrorControllerException;
import com.rest.documentManager.services.exceptions.ErrorLoginException;
import com.rest.documentManager.services.exceptions.ErrorValidExcpetion;
import com.rest.documentManager.services.exceptions.ForbidenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ErrorLoginException.class)
    public ResponseEntity<StandardError> unauthorizedLogin(ErrorLoginException ex, HttpServletRequest request) {
        StandardError error = new StandardError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ForbidenException.class)
    public ResponseEntity<StandardError> forbidenExcpetion(ForbidenException ex, HttpServletRequest request) {
        StandardError error = new StandardError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ErrorControllerException.class)
    public ResponseEntity<StandardError> errorController(ErrorControllerException ex, HttpServletRequest request) {
        StandardError error = new StandardError(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ErrorValidExcpetion.class)
    public ResponseEntity<StandardError> errorController(ErrorValidExcpetion ex, HttpServletRequest request) {
        StandardError error = new StandardError(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

//    @ExceptionHandler({ Exception.class })
//    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
//        StandardError apiError =
//                new StandardError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getLocalizedMessage(), request.getContextPath());
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                apiError);
//    }
}

