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
}
