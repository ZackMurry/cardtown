package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.user.UserDao;
import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.exception.LengthRequiredException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.*;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.JwtUtil;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private static final String PROFILE_PICTURE_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + ".cardtown" + File.separator + "static" + File.separator + "images" + File.separator + "pfp";
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

    @Autowired
    private TeamService teamService;

    @PostConstruct
    private void init() {
        File f = new File(PROFILE_PICTURE_DIRECTORY_PATH);
        if (!f.exists()) {
            logger.info("Profile picture directory {} not found. Creating...", f.getAbsolutePath());
            if (!f.mkdirs()) {
                logger.warn("Failed to create upload directory. Expect problems.");
            }
        }
    }

    /**
     * Gets a user's data by their email.
     * This is really only used by Spring Security
     *
     * @param email The user's email
     * @return A <code>User</code> representing the user's data
     * @throws UsernameNotFoundException If no users exist with the given email
     * @throws InternalServerException   If there is a <code>SQLException</code> in the DAO layer
     */
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userDao.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    /**
     * Gets a user's data by their email.
     * This method is used more than <code>UserService#loadUserByUsername</code> because I prefer optionals over exceptions
     * and the method name is a bit more fitting
     *
     * @param email The user's email
     * @return If found: an optional containing the user's data; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public Optional<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /**
     * Creates an account for a user with the specified details
     *
     * @param user User model to create in database. The password should not be encoded
     * @return An AuthenticationResponse containing the user's new JWT
     * @throws InternalServerException If an error occurred while creating a hash or secret key for the user
     * @throws BadRequestException     If any of the fields in the <code>User</code> object is null
     * @throws ResponseStatusException (Length required) If any of the following conditions are not met:
     *                                 password must be <= 55 chars, first name must be <= 32 chars, last name must be <= 32 chars, email must be <= 320 chars
     * @throws ResponseStatusException (Precondition failed) If an account already exists with the given email
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public AuthenticationResponse createUserAccount(@NonNull User user) {
        if (user.getPassword() == null || user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null) {
            throw new BadRequestException();
        }
        // todo required somewhat good password
        if (user.getPassword().length() > 55 ||
                user.getFirstName().length() > 32 ||
                user.getLastName().length() > 32 ||
                user.getEmail().length() > 320) {
            throw new LengthRequiredException();
        }
        final String plainTextPassword = user.getPassword();
        user.setPassword(encoder.encode(user.getPassword()));

        // generating and encrypting secret key
        // todo salt encryptionKey with email or something
        // SHA-256 hash the user's password
        // The role of the encryption key is to encrypt the secret key in the database
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

        final UserModel userModel = new UserModel(user, encryptedSecretKey, null, Optional.empty(), encryptionKey);
        final UUID userId = userDao.createAccount(userModel);
        return new AuthenticationResponse(buildJwtForUser(userModel), UUIDCompressor.compress(userId));
    }

    /**
     * Helper method for creating a new user account
     *
     * @param email     The email of the new account
     * @param firstName The first name of the new user
     * @param lastName  The last name of the new user
     * @param password  The (plaintext) password of this user
     * @return An <code>AuthenticationResponse</code> containing the user's new JWT
     * @throws InternalServerException If an error occurred while creating a hash or secret key for the user
     * @throws BadRequestException     If any of the fields in the <code>User</code> object is null
     * @throws ResponseStatusException (Length required) If any of the following conditions are not met:
     *                                 password must be <= 55 chars, first name must be <= 32 chars, last name must be <= 32 chars, email must be <= 320 chars
     * @throws ResponseStatusException (Precondition failed) If an account already exists with the given email
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     * @see UserService#createUserAccount(User) This method is an abstraction for this
     */
    public AuthenticationResponse createUserAccount(String email, String firstName, String lastName, String password) {
        return createUserAccount(new User(email, firstName, lastName, password));
    }

    /**
     * Finds if an account exists with a given email
     *
     * @param email The email to look for
     * @return A boolean representing the status of the account's existence
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public boolean accountExists(String email) {
        return userDao.accountExists(email);
    }

    /**
     * Deletes an account with a given email
     *
     * @param email Email of account to delete
     * @throws UserNotFoundException   If no user with this email was found
     * @throws InternalServerException If there was a <code>SQLException</code> in the DAO layer
     */
    public void deleteUserAccount(@NonNull String email) throws UserNotFoundException {
        userDao.deleteUser(email);
    }

    /**
     * Gets a user's secret key in terms of <code>String</code>s
     *
     * @param email         Email of user to find secret key of
     * @param encryptionKey Encryption key to use in order to decrypt the secret key. This should be in Base 64
     * @return The unencrypted secret key in Base64
     * @throws UserNotFoundException   If no user is found with this email
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     * @throws InternalServerException If there is an error decrypting the secret key
     * @see UserService#getUserSecretKey(String, String) This method is called after converting the <code>String</code>s to Base 64 and
     * the response is converted to Base64
     */
    public String getUserSecretKey(@NonNull String email, @NonNull String encryptionKey) throws UserNotFoundException {
        return Base64.encodeBase64String(getUserSecretKey(email, Base64.decodeBase64(encryptionKey)));
    }

    /**
     * Gets a user's (decrypted) secret key
     *
     * @param email         Email of user to find secret key of
     * @param encryptionKey Encryption key to use in order to decrypt the secret key
     * @return The unencrypted secret key
     * @throws UserNotFoundException   If no user is found with this email
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
     *
     * @param email Email of user to find id of
     * @return If user is found: an <code>Optional</code> containing the user's id; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public Optional<UUID> getIdByEmail(String email) {
        return userDao.getIdByEmail(email);
    }

    /**
     * Gets a user's data by their id
     *
     * @param userId Id of user to find
     * @return If user is found: an <code>Optional</code> containing the user's data; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code> in the DAO layer
     */
    public Optional<User> getUserById(UUID userId) {
        return userDao.findById(userId);
    }

    /**
     * Creates an authentication token for a user with the given details
     *
     * @param email    Email of user to authenticate
     * @param password Password of user
     * @return An <code>AuthenticationResponse</code> containing the created JWT
     * @throws org.springframework.security.core.AuthenticationException If the credentials are invalid
     * @throws InternalServerException                                   If a <code>SQLException</code> occurs in the DAO layer
     */
    public AuthenticationResponse createAuthenticationToken(@NonNull String email, @NonNull String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        // Generate SHA-256 hash of password as their encryption key (hash is 32 bytes)
        final byte[] encryptionKey = encryptionUtils.getSHA256Hash(password.getBytes(StandardCharsets.UTF_8));

        final UserModel userModel = getUserModelByEmail(email, encryptionKey).orElseThrow(InternalServerException::new);
        return new AuthenticationResponse(buildJwtForUser(userModel), UUIDCompressor.compress(userModel.getId()));
    }

    public String buildJwtForUser(@NonNull UserModel userModel) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("ek", Base64.encodeBase64String(userModel.getEncryptionKey()));
        claims.put("f_name", userModel.getFirstName());
        claims.put("l_name", userModel.getLastName());
        // todo include pfp url in jwt
        return jwtUtil.createToken(claims, userModel.getEmail());
    }

    /**
     * Creates a <code>ResponseUserDetails</code> from a user's id
     *
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

    /**
     * Generates a <code>UserModel</code> from a user's email and encryption key
     *
     * @param email         Email of user to generate a <code>UserModel</code> for
     * @param encryptionKey Encryption key of user
     * @return If user is found: an <code>Optional</code> containing the <code>UserModel</code>; else: <code>Optional.empty()</code>
     */
    public Optional<UserModel> getUserModelByEmail(@NonNull String email, @NonNull byte[] encryptionKey) {
        final User user = userDao.findByEmail(email).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        final byte[] secretKey = getUserSecretKey(email, encryptionKey);
        final byte[] teamSecretKey = teamService.getTeamSecretKeyByUser(user.getId(), secretKey).orElse(null);
        final Optional<UUID> teamId = teamService.getTeamIdByUserId(user.getId());
        return Optional.of(new UserModel(user, secretKey, teamSecretKey, teamId, encryptionKey));
    }

    /**
     * Generates a <code>UserModel</code> from a user's id and encryption key
     *
     * @param id         Id of user to generate a <code>UserModel</code> for
     * @param encryptionKey Encryption key of user
     * @return If user is found: an <code>Optional</code> containing the <code>UserModel</code>; else: <code>Optional.empty()</code>
     */
    public Optional<UserModel> getUserModelById(@NonNull UUID id, @NonNull byte[] encryptionKey) {
        final User user = userDao.findById(id).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        final byte[] secretKey = getUserSecretKey(user.getEmail(), encryptionKey);
        final byte[] teamSecretKey = teamService.getTeamSecretKeyByUser(user.getId(), secretKey).orElse(null);
        final Optional<UUID> teamId = teamService.getTeamIdByUserId(user.getId());
        return Optional.of(new UserModel(user, secretKey, teamSecretKey, teamId, encryptionKey));
    }

    /**
     * Regenerates a <code>UsernamePasswordAuthenticationToken</code> based on the current token and the user's password.
     * This is useful for re-fetching it after a user has joined a team.
     *
     * @param token    Current token of user
     * @param password Password of user
     * @return An up-to-date version of <code>token</code>
     * @throws BadRequestException If the user could not be found (the account was likely deleted)
     * @see UserService#getUserModelByEmail(String, byte[]) getUserModelByEmail(...) is called under the hood
     */
    public UsernamePasswordAuthenticationToken regenerateTokenDetails(@NonNull UsernamePasswordAuthenticationToken token, @NonNull String password) {
        final UserModel currentModel = (UserModel) token.getPrincipal();
        final UserModel model = getUserModelByEmail(
                currentModel.getEmail(),
                encryptionUtils.getSHA256Hash(password.getBytes(StandardCharsets.UTF_8))
        ).orElseThrow(BadRequestException::new);
        return new UsernamePasswordAuthenticationToken(model, null, model.getAuthorities());
    }

    /**
     * Updates the name of the current user
     *
     * @param name New name of user
     * @return An <code>AuthenticationResponse</code> containing the new jwt of the user
     */
    public AuthenticationResponse updateUserName(@NonNull FirstLastName name) {
        if (name.getFirst() == null || name.getFirst().length() > 32 || name.getLast() == null || name.getLast().length() > 32) {
            throw new BadRequestException();
        }
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDao.updateUserName(principal.getId(), name);
        principal.setFirstName(name.getFirst());
        principal.setLastName(name.getLast());
        // Return a new jwt for the user because the details in it have changed
        return new AuthenticationResponse(buildJwtForUser(principal), UUIDCompressor.compress(principal.getId()));
    }

    public void updateUserProfilePicture(@NonNull MultipartFile file) {
        // todo: support jpg
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".png")) {
            throw new BadRequestException();
        }
        final UUID userId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        final String compressedUserId = UUIDCompressor.compress(userId);
        // Delete old profile picture (if exists)
        final File oldPicture = new File(PROFILE_PICTURE_DIRECTORY_PATH + File.separator + userId + ".png");
        if (oldPicture.exists()) {
            if (!oldPicture.delete()) {
                throw new InternalServerException();
            }
        }
        // Create file in filesystem
        try {
            final Path copyLocation = Paths.get(PROFILE_PICTURE_DIRECTORY_PATH + File.separator + StringUtils.cleanPath(compressedUserId + ".png"));
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

}
