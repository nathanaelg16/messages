package com.nathanaelg.jasmine.user.auth.crud;

import com.google.common.collect.ImmutableMap;
import com.nathanaelg.jasmine.token.api.TokenService;
import com.nathanaelg.jasmine.user.auth.api.UserAuthenticationService;
import com.nathanaelg.jasmine.user.crud.api.UserCrudService;
import com.nathanaelg.jasmine.user.user.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class TokenAuthenticationService implements UserAuthenticationService {

    @NonNull
    TokenService tokens;

    @NonNull
    UserCrudService users;

    @NonNull
    PasswordEncoder passwordEncoder;

    @Override
    public Optional<String> login(String username, String password) {
        return users
                .findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> tokens.permanent(ImmutableMap.of("username", username))); //TODO: Change this to expiring when app is ready
    }

    @Override
    public Optional<User> findByToken(String token) {
        return Optional
                .of(tokens.verify(token))
                .map(map -> map.get("username"))
                .flatMap(users::findByUsername);
    }

    @Override
    public void logout(User user) {

    }
}
