package com.nathanaelg.jasmine.exception;

import com.nathanaelg.jasmine.date.TimeStamp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ExceptionResponse exceptionResponse = new ExceptionResponse(TimeStamp.getCurrentTimeStamp(), HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public final ResponseEntity<Object> handleResponseStatusExceptions(ResponseStatusException ex, WebRequest request) {
        ex.printStackTrace();
        ExceptionResponse exceptionResponse = new ExceptionResponse(TimeStamp.getCurrentTimeStamp(), ex.getStatus(), ex.getReason());
        return new ResponseEntity<>(exceptionResponse, ex.getStatus());
    }

    @ExceptionHandler(SQLException.class)
    public final ResponseEntity<Object> handleSQLExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ExceptionResponse exceptionResponse = new ExceptionResponse(TimeStamp.getCurrentTimeStamp(), HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while connecting to our databases.");
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> handleBadCredentialsExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ExceptionResponse exceptionResponse = new ExceptionResponse(TimeStamp.getCurrentTimeStamp(), HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }
}