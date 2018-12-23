package com.nathanaelg.jasmine.exception;

import org.springframework.http.HttpStatus;

public class ExceptionResponse {
    private String status;
    private String details;
    private String timestamp;

    public ExceptionResponse(String timestamp, HttpStatus status, String details) {
        this.timestamp = timestamp;
        this.status = status.toString();
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }

    public String getStatus() {
        return status;
    }
}