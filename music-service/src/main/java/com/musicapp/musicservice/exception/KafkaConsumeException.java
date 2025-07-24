package com.musicapp.musicservice.exception;

public class KafkaConsumeException extends RuntimeException {
    public KafkaConsumeException(String message) {
        super(message);
    }

    public KafkaConsumeException(String message, Throwable cause) {
      super(message, cause);
    }
}
