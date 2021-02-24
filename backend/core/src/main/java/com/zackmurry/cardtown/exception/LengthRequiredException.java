package com.zackmurry.cardtown.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LENGTH_REQUIRED)
public class LengthRequiredException extends RuntimeException {

    public LengthRequiredException() {
        super("Length required");
    }

    public LengthRequiredException(String message) {
        super(message);
    }

    public LengthRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public LengthRequiredException(Throwable cause) {
        super(cause);
    }

    public LengthRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
