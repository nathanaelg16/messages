package com.nathanaelg.jasmine.messages;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class MessageUpdatePriorityBean {
    @NotNull
    private Integer id;

    @Nullable
    private Integer priority;

    protected MessageUpdatePriorityBean() {

    }

    public Integer getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}