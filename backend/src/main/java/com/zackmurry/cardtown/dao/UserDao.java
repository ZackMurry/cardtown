package com.zackmurry.cardtown.dao;

import com.zackmurry.cardtown.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao {

    Optional<User> findByEmail(String email);

    HttpStatus createAccount(User user);

}
