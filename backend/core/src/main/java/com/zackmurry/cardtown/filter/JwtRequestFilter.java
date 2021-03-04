package com.zackmurry.cardtown.filter;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.exception.UserNotFoundException;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.service.TeamService;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.JwtUtil;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 *
 * from https://www.youtube.com/watch?v=X80nJ5T7YpE
 *
 * a filter for jwt configuration
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private JwtUtil jwtUtil;

    // Using a separate MessageDigest instead of using EncryptionUtils' so that the lock isn't interfered with by admin requests.
    // Thus, this is only used by Spring Boot Admin
    private final MessageDigest messageDigest;

    private final String adminEmail;

    private final String adminPassword;

    public JwtRequestFilter(Environment environment) throws NoSuchAlgorithmException {
        this.adminEmail = environment.getProperty("CARDTOWN_ADMIN_USERNAME");
        this.adminPassword = environment.getProperty("CARDTOWN_ADMIN_PASSWORD");
        this.messageDigest = MessageDigest.getInstance("SHA-256");
    }

    /**
     * Adds an additional filter before Spring Security returns a 403.
     * Compares the 'Authorization' header and allows authorization to a page if it is valid
     * todo unit tests?
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");


        if (authorizationHeader == null) {
            chain.doFilter(request, response);
            return;
        }

        if (authorizationHeader.startsWith("Bearer ")) {
            // Authentication using normal user workflow
            final String jwt = authorizationHeader.substring(7); // remove "Bearer " from the front
            String email;
            try {
                email = jwtUtil.extractSubject(jwt);
            } catch (MalformedJwtException | SignatureException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                final User user = userService.loadUserByUsername(email);
                final String encryptionKeyBase64 = jwtUtil.extractEncryptionKey(jwt);
                if (encryptionKeyBase64 == null) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
                byte[] encryptionKey = Base64.decodeBase64(encryptionKeyBase64);
                byte[] secretKey;
                try {
                    secretKey = userService.getUserSecretKey(email, encryptionKey);
                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                    throw new InternalServerException();
                }
                if (secretKey == null) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                if (jwtUtil.validateToken(jwt, user)) {
                    final UserModel model = userService.getUserModelByEmail(email, encryptionKey).orElseThrow(InternalServerException::new);
                    final var token = new UsernamePasswordAuthenticationToken(model, null, user.getAuthorities());
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }
        } else if (authorizationHeader.startsWith("Admin ")) {
            // Authentication for Spring Boot Admin Server
            // todo try to just combine this with the normal workflow
            final String[] parts = authorizationHeader.substring(6).split("\\|");
            if (parts.length != 2) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            final String reqEmail = parts[0];
            final String reqPassword = parts[1];
            if (!reqEmail.equals(adminEmail) || !reqPassword.equals(adminPassword)) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            byte[] encryptionKey;
            synchronized (this) {
                messageDigest.update(adminPassword.getBytes(StandardCharsets.UTF_8));
                encryptionKey = messageDigest.digest();
                messageDigest.reset();
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                final User user = userService.loadUserByUsername(reqEmail);
                byte[] secretKey;
                try {
                    secretKey = userService.getUserSecretKey(reqEmail, encryptionKey);
                } catch (UserNotFoundException e) {
                    e.printStackTrace();
                    throw new InternalServerException();
                }
                if (secretKey == null) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
                final UserModel model = userService.getUserModelByEmail(reqEmail, encryptionKey).orElseThrow(InternalServerException::new);
                var token = new UsernamePasswordAuthenticationToken(model, null, user.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }

}
