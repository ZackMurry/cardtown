package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.user.UserDao;
import com.zackmurry.cardtown.model.auth.AuthenticationResponse;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.JwtUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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
    public ResponseEntity<AuthenticationResponse> createUserAccount(User user) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // yikes 0.0
        if ((user.getPassword() == null || user.getPassword().length() > 55) ||
                (user.getFirstName() == null || user.getFirstName().length() > 32) ||
                (user.getLastName() == null || user.getLastName().length() > 32) ||
                (user.getEmail() == null || user.getEmail().length() > 320)) {
            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }
        String plainTextPassword = user.getPassword();
        user.setPassword(encoder.encode(user.getPassword()));

        // generating and encrypting secret key
        // todo salt encryptionKey with email or something
        final byte[] encryptionKey = EncryptionUtils.getSHA256Hash(plainTextPassword);
        if (encryptionKey == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SecretKey secretKey = EncryptionUtils.generateStrongAESKey(256);
        // aes encrypting the aes secret key 0.0
        byte[] encryptedSecretKey;
        try {
            encryptedSecretKey = EncryptionUtils.encryptAES(secretKey.getEncoded(), encryptionKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserModel userModel = new UserModel(user, encryptedSecretKey);
        HttpStatus status = userDao.createAccount(userModel);

        // put username and encryption key into jwt
        final Map<String, Object> claims = new HashMap<>();
        claims.put("ek", Base64.encodeBase64String(encryptionKey));
        String jwt = jwtUtil.createToken(claims, user.getUsername());

        return new ResponseEntity<>(new AuthenticationResponse(jwt), status);
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
    public ResponseEntity<AuthenticationResponse> createUserAccount(String email, String firstName, String lastName, String password) {
        return createUserAccount(new User(email, firstName, lastName, password));
    }

    public boolean accountExists(String email) {
        return userDao.accountExists(email);
    }

    public ResponseEntity<Void> deleteUserAccount(String email) {
        return new ResponseEntity<>(userDao.deleteUser(email));
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
}
