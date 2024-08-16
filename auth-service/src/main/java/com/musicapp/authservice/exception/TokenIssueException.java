package com.musicapp.authservice.exception;

public class TokenIssueException extends RuntimeException {
    public TokenIssueException(String message) {
        super(message);
    }
}
