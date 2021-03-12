package com.zackmurry.cardtown.dao.user;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao {

    /**
     * Finds a user with the given email
     *
     * @param email Email of user
     * @return If found: optional containing a <code>User</code>; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user's id by their email
     *
     * @param email Email of user
     * @return If user is found: optional containing a <code>UUID</code>; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    Optional<UUID> getIdByEmail(String email);

    /**
     * Creates an account with the given details
     *
     * @param user Details of user to create
     * @throws org.springframework.web.server.ResponseStatusException (Precondition failed) If a user with the specified email already exists
     * @throws InternalServerException                                If there is a <code>SQLException</code>
     */
    void createAccount(UserModel user);

    /**
     * Finds if an account with an email exists
     *
     * @param email Email to check for
     * @return A boolean representing the status of the account's existence
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    boolean accountExists(String email);

    /**
     * Deletes a user with a specified email
     *
     * @param email Email of user to delete
     * @throws UserNotFoundException   If there is no user found with that email
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    void deleteUser(String email) throws UserNotFoundException;

    /**
     * Gets the secret key of a user with a specified email
     *
     * @param email Email of user to find secret key of
     * @return If user is found: the user's secret key in Base 64; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    Optional<String> getEncryptedSecretKey(String email);

    /**
     * Finds a user with a specified id
     *
     * @param id Id of user to find
     * @return If user is found: optional containing the user's data; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    Optional<User> findById(UUID id);

}
