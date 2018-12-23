package com.nathanaelg.jasmine.user.auth.api;

import com.nathanaelg.jasmine.user.user.User;

import java.util.Optional;

public interface UserAuthenticationService {
    Optional<String> login(String username, String password);

    Optional<User> findByToken(String token);

    void logout(User user);
}