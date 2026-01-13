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
public class UploadKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String trackUploadedTopic;
    private final ObjectMapper objectMapper;

    @Autowired
    public UploadKafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${track-uploaded-topic}")
            String trackUploadedTopic,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.trackUploadedTopic = trackUploadedTopic;
        this.objectMapper = objectMapper;
    }

    public void trackUploaded(TrackUploadedEvent event) {
        String json = null;
        System.out.println("\n\n\n\n\n TRACK UPLOADED TO KAFKA \n\n\n\n\n");
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new KafkaProduceException(e.getMessage(), e);
        }
        kafkaTemplate.send(trackUploadedTopic, json);
    }
}
