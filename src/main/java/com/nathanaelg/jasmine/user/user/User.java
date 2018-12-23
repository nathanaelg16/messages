package com.nathanaelg.jasmine.user.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

@Value
public class User implements UserDetails {

    int id;

    @Size(max = 20)
    String username;

    String passwordHash;

    @JsonCreator
    public User(@JsonProperty("id") final Integer id, @JsonProperty("username") final String username, @JsonProperty("password") final String passwordHash) {
        super();
        this.id = requireNonNull(id);
        this.username = requireNonNull(username);
        this.passwordHash = requireNonNull(passwordHash);
    }

    @JsonIgnore
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @JsonIgnore
    public int getID() {
        return id;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return passwordHash;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
