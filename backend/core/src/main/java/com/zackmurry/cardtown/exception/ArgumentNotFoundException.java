package com.zackmurry.cardtown.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ArgumentNotFoundException extends RuntimeException {

    public ArgumentNotFoundException() {
        super("Argument not found");
    }

    public ArgumentNotFoundException(String message) {
        super(message);
    }

    public ArgumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentNotFoundException(Throwable cause) {
        super(cause);
    }

    public ArgumentNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}