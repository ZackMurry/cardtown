package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.auth.AuthenticationRequest;
import com.zackmurry.cardtown.model.auth.AuthenticationResponse;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest request) throws AuthenticationException {
        if (request.getPassword() == null || request.getPassword().length() > 64 || request.getPassword().length() < 8) {
            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final Map<String, Object> claims = new HashMap<>();

        // generate SHA-256 hash of password as their secret key (hash is 32 bytes)
        String encryptionKey = EncryptionUtils.getSHA256HashHex(request.getPassword());
        if (encryptionKey == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        claims.put("ek", encryptionKey);

        String jwt = jwtUtil.createToken(claims, userDetails.getUsername());
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
