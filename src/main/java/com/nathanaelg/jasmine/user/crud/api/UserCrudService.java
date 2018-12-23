package com.nathanaelg.jasmine.user.crud.api;

import com.nathanaelg.jasmine.user.user.User;
import com.nathanaelg.jasmine.user.user.UserRegistration;

import java.util.Optional;

public interface UserCrudService {
    User save(UserRegistration user);

    Optional<User> find(Integer id);

    Optional<User> findByUsername(String username);

    boolean findUsername(String username) throws Exception;
}
