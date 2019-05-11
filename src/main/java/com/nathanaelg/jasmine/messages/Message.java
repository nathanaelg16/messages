package com.nathanaelg.jasmine.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    private int id;
    private String recipient;
    private String sender;
    private String title;
    private String message;
    private String timestamp;
    private int priority;

    public Message(int id, String recipient, String sender, String title, String message, String timestamp, Integer priority) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
        this.recipient = recipient;
        this.priority = (priority == null) ? 0 : priority;
    }

    public Message(int id, String recipient, String sender, String title, String message, String timestamp) {
        this.id = id;
        this.recipient = recipient;
        this.sender = sender;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.priority = 0;
    }

    @JsonIgnore
    public int getID() {
        return id;
    }

    @JsonIgnore
    public void setID(int id) {
        this.id = id;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    @JsonIgnore
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonIgnore
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty
    public String getTimestamp() {
        return timestamp;
    }

    @JsonIgnore
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonIgnore
    public int getPriority() {
        return priority;
    }

    @JsonIgnore
    public void setPriority(Integer priority) {
        this.priority = (priority == null) ? 0 : priority;
    }

    @JsonIgnore
    public String getSender() {
        return sender;
    }

    @JsonIgnore
    public void setSender(String sender) {
        this.sender = sender;
    }

    @JsonProperty
    public String getRecipient() {
        return recipient;
    }

    @JsonIgnore
    public void setRecipient(String recipient) {
        this.recipient = recipient;
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