package com.nathanaelg.jasmine.messages;

import javax.validation.constraints.NotNull;

public class MessageUpdateBean {

    @NotNull
    private String recipient;

    @NotNull
    private String title;

    @NotNull
    private String message;

    private int priority;

    protected MessageUpdateBean() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
