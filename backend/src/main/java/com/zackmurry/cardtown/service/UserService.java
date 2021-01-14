package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.user.UserDao;
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

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.findByEmail(s).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(s);
        }
        return user;
    }

    public Optional<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /**
     * creates an account for a user with the specified details
     * @param user user model to create in database. password should not be encoded
     * @return a <code>ResponseEntity</code> with an AuthenticationResponse. this represents the status and access token for the new user
     */
    public AuthenticationResponse createUserAccount(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        // yikes 0.0
        if ((user.getPassword() == null || user.getPassword().length() > 55) ||
                (user.getFirstName() == null || user.getFirstName().length() > 32) ||
                (user.getLastName() == null || user.getLastName().length() > 32) ||
                (user.getEmail() == null || user.getEmail().length() > 320)) {
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED);
        }
        String plainTextPassword = user.getPassword();
        user.setPassword(encoder.encode(user.getPassword()));

        // generating and encrypting secret key
        // todo salt encryptionKey with email or something
        final byte[] encryptionKey = EncryptionUtils.getSHA256Hash(plainTextPassword.getBytes(StandardCharsets.UTF_8));
        if (encryptionKey == null) {
            throw new InternalServerException();
        }

        SecretKey secretKey = EncryptionUtils.generateStrongAESKey(256);
        // aes encrypting the aes secret key 0.0
        byte[] encryptedSecretKey;
        try {
            encryptedSecretKey = EncryptionUtils.encryptAES(secretKey.getEncoded(), encryptionKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }

        UserModel userModel = new UserModel(user, encryptedSecretKey);
        userDao.createAccount(userModel);

        // put username and encryption key into jwt
        final Map<String, Object> claims = new HashMap<>();
        claims.put("ek", Base64.encodeBase64String(encryptionKey));
        String jwt = jwtUtil.createToken(claims, user.getUsername());

        return new AuthenticationResponse(jwt);
    }

    /**
     * helper method for creating a new user account
     * @param email the email of the new account
     * @param firstName the first name of the new user
     * @param lastName the last name of the new user
     * @param password the password of this user. this should not be encoded.
     * @return a <code>ResponseEntity</code> with an AuthenticationResponse. this represents the status and access token for the new user
     * @see UserService#createUserAccount(User)
     */
    public AuthenticationResponse createUserAccount(String email, String firstName, String lastName, String password) {
        return createUserAccount(new User(email, firstName, lastName, password));
    }

    public boolean accountExists(String email) {
        return userDao.accountExists(email);
    }

    public void deleteUserAccount(String email) {
        try {
            userDao.deleteUser(email);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    public String getUserSecretKey(@NonNull String email, @NonNull String encryptionKey) {
        return Base64.encodeBase64String(getUserSecretKey(email, Base64.decodeBase64(encryptionKey)));
    }

    public byte[] getUserSecretKey(String email, byte[] encryptionKey) {
        String encryptedSecretKey = userDao.getEncryptedSecretKey(email);
        if (encryptedSecretKey == null) {
            return null;
        }
        try {
            return EncryptionUtils.decryptAES(Base64.decodeBase64(encryptedSecretKey), encryptionKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<UUID> getIdByEmail(String email) {
        return userDao.getIdByEmail(email);
    }

    public Optional<User> getUserById(UUID userId) {
        return userDao.findById(userId);
    }

    public AuthenticationResponse createAuthenticationToken(@NonNull AuthenticationRequest request) {
        if (request.getPassword() == null || request.getPassword().length() > 64 || request.getPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED);
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        final UserDetails userDetails = loadUserByUsername(request.getEmail());
        final Map<String, Object> claims = new HashMap<>();

        // generate SHA-256 hash of password as their secret key (hash is 32 bytes)
        String encryptionKey = EncryptionUtils.getSHA256HashBase64(request.getPassword());
        if (encryptionKey == null) {
            throw new InternalServerException();
        }
        claims.put("ek", encryptionKey);

        String jwt = jwtUtil.createToken(claims, userDetails.getUsername());
        return new AuthenticationResponse(jwt);
    }

    public ResponseUserDetails getResponseUserDetailsById(@NonNull UUID id) throws UserNotFoundException {
        final User user = userDao.findById(id).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return ResponseUserDetails.fromUser(user);
    }

}
