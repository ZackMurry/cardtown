package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.user.UserDao;
import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.*;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.JwtUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EncryptionUtils encryptionUtils;

    /**
     * Gets a user's data by their email.
     * This is really only used by Spring Security
     * @param email The user's email
     * @return A <code>User</code> representing the user's data
     * @throws UsernameNotFoundException If no users exist with the given email
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userDao.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return user;
    }

    /**
     * Gets a user's data by their email.
     * This method is used more than <code>UserService#loadUserByUsername</code> because I prefer optionals over exceptions
     * and the method name is a bit more fitting
     * @param email The user's email
     * @return If found: an optional containing the user's data; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public Optional<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /**
     * Creates an account for a user with the specified details
     * @param user User model to create in database. The password should not be encoded
     * @return An AuthenticationResponse containing the user's new JWT
     * @throws InternalServerException If an error occurred while creating a hash or secret key for the user
     * @throws BadRequestException If any of the fields in the <code>User</code> object is null
     * @throws ResponseStatusException (Length required) If any of the following conditions are not met:
     * password must be <= 55 chars, first name must be <= 32 chars, last name must be <= 32 chars, email must be <= 320 chars
     * @throws ResponseStatusException (Precondition failed) If an account already exists with the given email
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public AuthenticationResponse createUserAccount(@NonNull User user) {
        // yikes 0.0
        if (user.getPassword() == null || user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null) {
            throw new BadRequestException();
        }
        // todo required somewhat good password
        if (user.getPassword().length() > 55 ||
            user.getFirstName().length() > 32 ||
            user.getLastName().length() > 32 ||
            user.getEmail().length() > 320) {
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED);
        }
        final String plainTextPassword = user.getPassword();
        user.setPassword(encoder.encode(user.getPassword()));

        // generating and encrypting secret key
        // todo salt encryptionKey with email or something
        // SHA-256 hash the user's password
        final byte[] encryptionKey = encryptionUtils.getSHA256Hash(plainTextPassword.getBytes(StandardCharsets.UTF_8));
        if (encryptionKey == null) {
            throw new InternalServerException();
        }

        // Creating the user's new secret key
        final SecretKey secretKey = EncryptionUtils.generateStrongAESKey(256);
        // aes encrypting the aes secret key 0.0
        // Encrypting the secret key with the user's password's SHA-256 hash
        byte[] encryptedSecretKey;
        try {
            encryptedSecretKey = EncryptionUtils.encryptAES(secretKey.getEncoded(), encryptionKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }

        final UserModel userModel = new UserModel(user, encryptedSecretKey);
        userDao.createAccount(userModel);

        // Put username and encryption key into JWT
        final Map<String, Object> claims = new HashMap<>();
        claims.put("ek", Base64.encodeBase64String(encryptionKey));
        final String jwt = jwtUtil.createToken(claims, user.getUsername());

        return new AuthenticationResponse(jwt);
    }

    /**
     * Helper method for creating a new user account
     * @see UserService#createUserAccount(User) This method is an abstraction for this
     * @param email The email of the new account
     * @param firstName The first name of the new user
     * @param lastName The last name of the new user
     * @param password The password of this user. this should not be encoded.
     * @return An <code>AuthenticationResponse</code> containing the user's new JWT
     * @throws InternalServerException If an error occurred while creating a hash or secret key for the user
     * @throws BadRequestException If any of the fields in the <code>User</code> object is null
     * @throws ResponseStatusException (Length required) If any of the following conditions are not met:
     * password must be <= 55 chars, first name must be <= 32 chars, last name must be <= 32 chars, email must be <= 320 chars
     * @throws ResponseStatusException (Precondition failed) If an account already exists with the given email
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public AuthenticationResponse createUserAccount(String email, String firstName, String lastName, String password) {
        return createUserAccount(new User(email, firstName, lastName, password));
    }

    /**
     * Finds if an account exists with a given email
     * @param email The email to look for
     * @return A boolean representing the status of the account's existence
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public boolean accountExists(String email) {
        return userDao.accountExists(email);
    }

    /**
     * Deletes an account with a given email
     * @param email Email of account to delete
     * @throws UserNotFoundException If no user with this email was found
     * @throws InternalServerException If there was a <code>SQLException</code> in the DAO layer
     */
    public void deleteUserAccount(@NonNull String email) throws UserNotFoundException {
        userDao.deleteUser(email);
    }

    /**
     * Gets a user's secret key in terms of <code>String</code>s
     * @see UserService#getUserSecretKey(String, String) This method is called after converting the <code>String</code>s to Base 64 and
     * the response is converted to Base64
     * @param email Email of user to find secret key of
     * @param encryptionKey Encryption key to use in order to decrypt the secret key. This should be in Base 64
     * @return The unencrypted secret key in Base64
     * @throws UserNotFoundException If no user is found with this email
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     * @throws InternalServerException If there is an error decrypting the secret key
     */
    public String getUserSecretKey(@NonNull String email, @NonNull String encryptionKey) throws UserNotFoundException {
        return Base64.encodeBase64String(getUserSecretKey(email, Base64.decodeBase64(encryptionKey)));
    }

    /**
     * Gets a user's (decrypted) secret key
     * @param email Email of user to find secret key of
     * @param encryptionKey Encryption key to use in order to decrypt the secret key
     * @return The unencrypted secret key
     * @throws UserNotFoundException If no user is found with this email
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     * @throws InternalServerException If there is an error decrypting the secret key
     */
    public byte[] getUserSecretKey(String email, byte[] encryptionKey) throws UserNotFoundException {
        final String encryptedSecretKey = userDao.getEncryptedSecretKey(email).orElse(null);
        if (encryptedSecretKey == null) {
            throw new UserNotFoundException();
        }
        try {
            return EncryptionUtils.decryptAES(Base64.decodeBase64(encryptedSecretKey), encryptionKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    /**
     * Gets a user's id by their email
     * @param email Email of user to find id of
     * @return If user is found: an <code>Optional</code> containing the user's id; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public Optional<UUID> getIdByEmail(String email) {
        return userDao.getIdByEmail(email);
    }

    /**
     * Gets a user's data by their id
     * @param userId Id of user to find
     * @return If user is found: an <code>Optional</code> containing the user's data; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public Optional<User> getUserById(UUID userId) {
        return userDao.findById(userId);
    }

    /**
     * Creates an authentication token for a user with the given details
     * @param email Email of user to authenticate
     * @param password Password of user
     * @return An <code>AuthenticationResponse</code> containing the created JWT
     * @throws org.springframework.security.core.AuthenticationException If the credentials are invalid
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public AuthenticationResponse createAuthenticationToken(@NonNull String email, @NonNull String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        final UserDetails userDetails = loadUserByUsername(email);
        final Map<String, Object> claims = new HashMap<>();

        // generate SHA-256 hash of password as their secret key (hash is 32 bytes)
        final String encryptionKey = encryptionUtils.getSHA256HashBase64(password);
        claims.put("ek", encryptionKey);

        final String jwt = jwtUtil.createToken(claims, userDetails.getUsername());
        return new AuthenticationResponse(jwt);
    }

    /**
     * Creates a <code>ResponseUserDetails</code> from a user's id
     * @param id Id of the user
     * @return If user is found: an <code>Optional</code> containing the <code>ResponseUserDetails</code>;
     * if not found: <code>Optional.empty()</code>
     */
    public Optional<ResponseUserDetails> getResponseUserDetailsById(@NonNull UUID id) {
        final User user = userDao.findById(id).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(ResponseUserDetails.fromUser(user));
    }

}
