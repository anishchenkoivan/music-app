package com.musicapp.streamingservice.exception;

public class AudioSaveException extends RuntimeException {
    public AudioSaveException(String message) {
        super(message);
    }
    public AudioSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
