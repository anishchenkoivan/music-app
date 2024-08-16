package com.musicapp.mainservice.exception;

import org.springframework.http.HttpStatusCode;

public class AuthException extends RuntimeException {
    private HttpStatusCode statusCode;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
