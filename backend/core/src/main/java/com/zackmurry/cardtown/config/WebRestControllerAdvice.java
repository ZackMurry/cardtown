package com.zackmurry.cardtown.config;

import com.zackmurry.cardtown.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class WebRestControllerAdvice {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadCredentialsException(BadCredentialsException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthenticationException(AuthenticationException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleMalformedJwtException(MalformedJwtException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleExpiredJwtException(ExpiredJwtException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(BadRequestException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(CardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCardNotFoundException(CardNotFoundException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbiddenException(ForbiddenException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerException(InternalServerException exception, HttpServletResponse response) {
        exception.printStackTrace();
        return exception.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(UserNotFoundException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(RequestRejectedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRequestRejectedException(RequestRejectedException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

    @ExceptionHandler(PayloadTooLargeException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String handlePayloadTooLargeException(PayloadTooLargeException exception, HttpServletResponse response) {
        return exception.getMessage();
    }

}
