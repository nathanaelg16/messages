package com.nathanaelg.jasmine.user.controller;

import com.nathanaelg.jasmine.user.auth.api.UserAuthenticationService;
import com.nathanaelg.jasmine.user.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class SecuredUsersController {

    @NonNull
    private UserAuthenticationService authentication;

    @GetMapping("/current")
    User getCurrentUser(@AuthenticationPrincipal final User user) {
        return user;
    }

    @GetMapping("/logout")
    boolean logout(@AuthenticationPrincipal final User user) {
        authentication.logout(user);
        return true;
    }
}
