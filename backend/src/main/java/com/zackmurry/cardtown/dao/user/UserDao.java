package com.zackmurry.cardtown.dao.user;

import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao {

    /**
     * finds a user with the given email
     * @param email email of user
     * @return if found: optional containing <code>User</code>; if not found: <code>Optional.empty()</code>
     */
    Optional<User> findByEmail(String email);

    Optional<UUID> getIdByEmail(String email);

    void createAccount(UserModel user);

    boolean accountExists(String email);

    void deleteUser(String email) throws UserNotFoundException;

    String getEncryptedSecretKey(String email);

    Optional<User> findById(UUID id);

}
