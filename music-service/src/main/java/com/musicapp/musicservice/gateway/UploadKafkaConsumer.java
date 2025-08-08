package com.musicapp.musicservice.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapp.musicservice.exception.KafkaConsumeException;
import com.musicapp.musicservice.gateway.event.TrackDataUploadedEvent;
import com.musicapp.musicservice.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UploadKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final TrackService trackService;

    @Autowired
    public UploadKafkaConsumer(ObjectMapper objectMapper, TrackService trackService) {
        this.objectMapper = objectMapper;
        this.trackService = trackService;
    }

    @KafkaListener(topics = "${track-uploaded-topic}", groupId = "${track-group}")
    public void onTrackDataUploaded(String event) {
        try {
            TrackDataUploadedEvent trackDataUploadedEvent = objectMapper.readValue(event, TrackDataUploadedEvent.class);
            trackService.trackDataUploaded(trackDataUploadedEvent);
        } catch (JsonProcessingException e) {
            throw new KafkaConsumeException(e.getMessage(), e);
        }
    }
}
