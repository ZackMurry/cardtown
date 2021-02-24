package com.zackmurry.cardtown.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
public class PayloadTooLargeException extends RuntimeException {

    public PayloadTooLargeException() {
        super("Internal server error");
    }

    public PayloadTooLargeException(String message) {
        super(message);
    }

    public PayloadTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayloadTooLargeException(Throwable cause) {
        super(cause);
    }

    public PayloadTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
