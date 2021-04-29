package com.zackmurry.cardtown.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class EntityDeletedException extends RuntimeException {

    public EntityDeletedException() {
        super("Entity deleted");
    }

    public EntityDeletedException(String message) {
        super(message);
    }

    public EntityDeletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityDeletedException(Throwable cause) {
        super(cause);
    }

    protected EntityDeletedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
