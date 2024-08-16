package com.musicapp.mainservice.exception;

import org.springframework.http.HttpStatusCode;

public class UserServiceException extends RuntimeException {
    private HttpStatusCode statusCode;

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
