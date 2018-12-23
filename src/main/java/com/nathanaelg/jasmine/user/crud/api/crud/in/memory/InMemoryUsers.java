/*
package com.nathanaelg.jasmine.user.crud.api.crud.in.memory;

import com.nathanaelg.jasmine.user.crud.api.UserCrudService;
import com.nathanaelg.jasmine.user.user.User;
import com.nathanaelg.jasmine.user.user.UserRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

//@Service
final class InMemoryUsers implements UserCrudService {

    private static int userID = 0;
    Map<Integer, User> users = new HashMap<>();

    @Override
    public User save(final UserRegistration user) {
        userID++;
        return users.put(userID, new User(userID, user.getUsername(), user.getPassword()));
    }

    @Override
    public Optional<User> find(final Integer id) {
        return ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users
                .values()
                .stream()
                .filter(u -> Objects.equals(username, u.getUsername()))
                .findFirst();
    }

    @Override
    public boolean findUsername(String username) throws Exception {
        return findByUsername(username).isPresent();
    }
}*/
