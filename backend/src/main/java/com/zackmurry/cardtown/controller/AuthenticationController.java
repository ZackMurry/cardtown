package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.filter.JwtRequestFilter;
import com.zackmurry.cardtown.model.auth.AuthenticationRequest;
import com.zackmurry.cardtown.model.auth.AuthenticationResponse;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.JwtUtil;
import org.apache.tomcat.util.codec.binary.Base64;
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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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

        try {
            Cipher cipher = Cipher.getInstance("AES");
            final SecretKeySpec secretKey = new SecretKeySpec(JwtUtil.JWT_PWD_SECRET_KEY, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final String encryptedString = Base64.encodeBase64String(cipher.doFinal(request.getPassword().getBytes()));
            claims.put("pwd", encryptedString);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        String jwt = jwtUtil.createToken(claims, userDetails.getUsername());
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
