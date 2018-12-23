package com.nathanaelg.jasmine.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;

public class Message {

    private String recipient;
    private String sender;
    private String title;
    private String message;
    private String timestamp;
    private int priority;

    public Message(String recipient, String sender, String title, String message, String timestamp, @NotNull Integer priority) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
        this.recipient = recipient;
        this.priority = priority;
    }

    public Message(String recipient, String sender, String title, String message, String timestamp) {
        this.recipient = recipient;
        this.sender = sender;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.priority = 0;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @JsonIgnore
    public int getPriority() {
        return priority;
    }

    @JsonIgnore
    public String getSender() {
        return sender;
    }

    @JsonIgnore
    public String getRecipient() {
        return recipient;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "Message{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}