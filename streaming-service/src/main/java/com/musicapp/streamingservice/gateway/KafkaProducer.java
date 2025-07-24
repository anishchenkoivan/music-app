package com.musicapp.streamingservice.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapp.streamingservice.exception.KafkaProduceException;
import com.musicapp.streamingservice.gateway.event.TrackUploadedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String trackUploadedTopic;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${track-uploaded-topic}")
            String trackUploadedTopic,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.trackUploadedTopic = trackUploadedTopic;
        this.objectMapper = objectMapper;
    }

    public void trackUploaded(UUID trackId) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(new TrackUploadedEvent(trackId));
        } catch (JsonProcessingException e) {
            throw new KafkaProduceException(e.getMessage(), e);
        }
        kafkaTemplate.send(trackUploadedTopic, json);
    }
}
