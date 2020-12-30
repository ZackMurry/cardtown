package com.zackmurry.cardtown.filter;

import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.JwtUtil;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    private JwtUtil jwtUtil;

    /**
     * adds an additional filter before Spring Security returns a 403
     * compares the 'Authorization' header and allows authorization to a page if it is valid
     *
     * @param request incoming http request
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // remove "Bearer " from the front
            try {
                email = jwtUtil.extractSubject(jwt);
            } catch (MalformedJwtException | SignatureException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = (User) userService.loadUserByUsername(email);

            // getting user's encryption key for decrypting their secret key
            final String encryptionKeyBase64 = jwtUtil.extractEncryptionKey(jwt);
            if (encryptionKeyBase64 == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            final byte[] encryptionKey = Base64.decodeBase64(encryptionKeyBase64);

            byte[] secretKey = userService.getUserSecretKey(email, encryptionKey);
            if (secretKey == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }
            UserModel model = new UserModel(user, encryptionKey);
            if (jwtUtil.validateToken(jwt, user)) {
                var token = new UsernamePasswordAuthenticationToken(model, null, user.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        chain.doFilter(request, response);
    }

}
