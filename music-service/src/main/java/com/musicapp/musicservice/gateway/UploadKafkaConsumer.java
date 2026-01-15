package com.musicapp.musicservice.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapp.musicservice.entity.TrackData;
import com.musicapp.musicservice.exception.KafkaConsumeException;
import com.musicapp.musicservice.gateway.event.TrackDataUploadedEvent;
import com.musicapp.musicservice.service.AlbumService;
import com.musicapp.musicservice.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UploadKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final TrackService trackService;
    private final AlbumService albumService;

    @Autowired
    public UploadKafkaConsumer(ObjectMapper objectMapper, TrackService trackService, AlbumService albumService) {
        this.objectMapper = objectMapper;
        this.trackService = trackService;
        this.albumService = albumService;
    }

    @KafkaListener(topics = "${track-uploaded-topic}", groupId = "${track-group}")
    public void onTrackDataUploaded(String event) {
        try {
            TrackDataUploadedEvent trackDataUploadedEvent = objectMapper.readValue(event, TrackDataUploadedEvent.class);
            TrackData trackData = trackService.trackDataUploaded(trackDataUploadedEvent);
            albumService.createSingleFromRawData(trackData);
        } catch (JsonProcessingException e) {
            throw new KafkaConsumeException(e.getMessage(), e);
        }
    }
}
