package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.AuthenticationRequest;
import com.zackmurry.cardtown.model.AuthenticationResponse;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.JwtUtil;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest request) throws Exception {
        if (request.getPassword() == null || request.getPassword().length() > 64) {
            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            System.out.println("bad credentials");
            throw new Exception("Incorrect username or password", e);
        } catch (AuthenticationException e) {
            System.out.println("authentication exception");
            throw new Exception("Bad authentication attempt", e);
        } catch (IllegalArgumentException e) {
            System.out.println("password cannot be null");
            throw new Exception("password cannot be null", e);
        } catch (MalformedJwtException e) {
            System.out.println("malformed jwt");
            throw new Exception("jwt malformed", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}
