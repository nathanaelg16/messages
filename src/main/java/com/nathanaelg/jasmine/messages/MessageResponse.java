package com.nathanaelg.jasmine.messages;

import org.springframework.http.HttpStatus;

public class MessageResponse {
    private String status;
    private String details;
    private String timestamp;

    public MessageResponse(String timestamp, HttpStatus status, String details) {
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