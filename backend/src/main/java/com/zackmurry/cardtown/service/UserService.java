package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.UserDao;
import com.zackmurry.cardtown.model.AuthenticationResponse;
import com.zackmurry.cardtown.model.User;
import com.zackmurry.cardtown.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        // yikes 0.0
        if ((user.getPassword() == null || user.getPassword().length() > 64) ||
                (user.getFirstName() == null || user.getFirstName().length() > 32) ||
                (user.getLastName() == null || user.getLastName().length() > 32) ||
                (user.getEmail() == null || user.getEmail().length() > 320)) {
            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        HttpStatus status = userDao.createAccount(user);
        String jwt = jwtUtil.generateToken(user);
        return new ResponseEntity<>(new AuthenticationResponse(jwt), status);
    }

    /**
     * helper method for creating a new user account
     * @param email the email of the new account
     * @param firstName the first name of the new user
     * @param lastName the last name of the new user
     * @param password the password of this user
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

}
