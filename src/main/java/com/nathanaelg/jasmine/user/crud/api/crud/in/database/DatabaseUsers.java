package com.nathanaelg.jasmine.user.crud.api.crud.in.database;

import com.nathanaelg.jasmine.Database;
import com.nathanaelg.jasmine.user.crud.api.UserCrudService;
import com.nathanaelg.jasmine.user.user.User;
import com.nathanaelg.jasmine.user.user.UserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class DatabaseUsers implements UserCrudService {

    private final PasswordEncoder passwordEncoder;
    private final Database database;

    @Autowired
    public DatabaseUsers(PasswordEncoder passwordEncoder, Database database) {
        this.passwordEncoder = passwordEncoder;
        this.database = database;
    }

    @Override
    public User save(final @Valid UserRegistration user) {
        String username = user.getUsername();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        try {
            database.executeUpdate("INSERT INTO " + Database.Tables.USERS + " VALUES (null, ?, ?);", username, encodedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return findByUsername(username).get();
    }

    @Override
    public Optional<User> find(@NotNull final Integer id) {
        try {
            ResultSet resultSet = database.executeQuery("SELECT * FROM " + Database.Tables.USERS + " WHERE id = ?;", id);
            if (resultSet.next()) {
                return Optional.of(new User(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("pass")));
            } else {
                throw new BadCredentialsException("User not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(@NotNull String username) {
        try {
            ResultSet resultSet = database.executeQuery("SELECT * FROM " + Database.Tables.USERS + " WHERE username = ?;", username);
            if (resultSet.next()) {
                return Optional.of(new User(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("pass")));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public boolean findUsername(@NotNull String username) throws SQLException {
        return database.checkExists(Database.Tables.USERS, "username = ?", username);
    }
}
