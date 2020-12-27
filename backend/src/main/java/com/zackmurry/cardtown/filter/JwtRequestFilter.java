package com.zackmurry.cardtown.filter;

import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.service.UserService;
import com.zackmurry.cardtown.util.JwtUtil;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 *
 * from https://www.youtube.com/watch?v=X80nJ5T7YpE
 *
 * a filter for jwt configuration
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final byte[] TEMPORARY_JWT_PWD_SECRET_KEY = {
            0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79
    };

    @Autowired
    private UserService userDetailsService;

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
        // todo could look in the jwt cookie as well
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
            User user = (User) userDetailsService.loadUserByUsername(email);

            // getting user's private key for encryption
            String plainTextPass = null;
            try {
                plainTextPass = jwtUtil.extractPassword(jwt);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            user.setPassword(plainTextPass);
            if (jwtUtil.validateToken(jwt, user)) {
                var token = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        chain.doFilter(request, response);
    }

}
