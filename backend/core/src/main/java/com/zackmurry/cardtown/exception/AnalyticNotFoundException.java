package com.zackmurry.cardtown.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AnalyticNotFoundException extends RuntimeException {

    public AnalyticNotFoundException() {
        super("Analytic not found");
    }

    public AnalyticNotFoundException(String message) {
        super(message);
    }

    public AnalyticNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnalyticNotFoundException(Throwable cause) {
        super(cause);
    }

    public AnalyticNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
