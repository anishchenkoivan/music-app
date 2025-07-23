package com.musicapp.streamingservice.exception;

public class KafkaProduceException extends RuntimeException {
    public KafkaProduceException(String message) {
        super(message);
    }
    public KafkaProduceException(String message, Throwable cause) {
        super(message, cause);
    }
}
