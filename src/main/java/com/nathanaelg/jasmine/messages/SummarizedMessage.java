package com.nathanaelg.jasmine.messages;

import javax.validation.constraints.NotNull;

public final class SummarizedMessage {

    @NotNull
    private int id;

    @NotNull
    private String title;

    @NotNull
    private boolean read;

    @NotNull
    private int priority;

    public SummarizedMessage(final int id, final String title, final boolean read, final int priority) {
        this.id = id;
        this.title = title;
        this.read = read;
        this.priority = priority;
    }

    public int getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isRead() {
        return read;
    }

    @Override
    public String toString() {
        return "SummarizedMessage{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", read=" + read +
                '}';
    }
}
