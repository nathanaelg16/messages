package com.nathanaelg.jasmine.user.controller;

import com.nathanaelg.jasmine.user.auth.api.UserAuthenticationService;
import com.nathanaelg.jasmine.user.crud.api.UserCrudService;
import com.nathanaelg.jasmine.user.user.User;
import com.nathanaelg.jasmine.user.user.UserRegistration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/public/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class PublicUsersController {

    private static final String SECRET = "89410927187820";

    @NonNull
    private UserAuthenticationService authentication;

    @NonNull
    private UserCrudService users;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/register")
    String register(@RequestParam("secret") final String secret, @RequestParam("username") final String username, @RequestParam("password") final String password) {
        if (!secret.equals(SECRET)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        Optional<User> user = users.findByUsername(username);
        if (user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        } else {
            users.save(new UserRegistration(username, password));
            return login(username, password);
        }
    }

    @PostMapping("/login")
    String login(@RequestParam("username") final String username, @RequestParam("password") final String password) {
        return authentication
                .login(username, password)
                .orElseThrow(() -> new BadCredentialsException("Invalid login and/or password"));
    }
}