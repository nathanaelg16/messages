package com.nathanaelg.jasmine.messages;

import javax.validation.constraints.NotNull;

public class MessageStatusBean {

    @NotNull
    private String title;

    @NotNull
    private String recipient;

    @NotNull
    private int priority;

    @NotNull
    private boolean read;

    private String readTimeStamp;

    public MessageStatusBean(@NotNull String title, @NotNull String recipient, @NotNull int priority, @NotNull boolean read) {
        this.title = title;
        this.recipient = recipient;
        this.priority = priority;
        this.read = read;
    }

    public MessageStatusBean(@NotNull String title, @NotNull String recipient, @NotNull int priority, @NotNull boolean read, String readTimeStamp) {
        this.title = title;
        this.recipient = recipient;
        this.priority = priority;
        this.read = read;
        this.readTimeStamp = readTimeStamp;
    }

    public String getTitle() {
        return title;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isRead() {
        return read;
    }

    public String getReadTimeStamp() {
        return readTimeStamp;
    }

    @Override
    public String toString() {
        return "MessageStatusBean{" +
                "title='" + title + '\'' +
                ", recipient='" + recipient + '\'' +
                ", read=" + read +
                ", readTimeStamp='" + readTimeStamp + '\'' +
                '}';
    }
}
