package com.zackmurry.cardtown.dao.user;

import com.zackmurry.cardtown.model.auth.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao {

    Optional<User> findByEmail(String email);

    Optional<UUID> getIdByEmail(String email);

    HttpStatus createAccount(User user);

    boolean accountExists(String email);

    HttpStatus deleteUser(String email);

}
