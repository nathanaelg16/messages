package com.nathanaelg.jasmine.user.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;

public class UserRegistration {
    @Size(max = 20, message = "Username must not exceed 20 characters")
    private String username;
    private String password;

    @JsonCreator
    public UserRegistration(@JsonProperty("username") final String username, @JsonProperty("password") final String password) {
        this.username = username;
        this.password = password;
    }

    @JsonIgnore
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "UserRegistration{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
