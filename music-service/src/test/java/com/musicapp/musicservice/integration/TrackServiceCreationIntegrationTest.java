package com.musicapp.musicservice.integration;

import com.musicapp.musicservice.dto.request.TrackDataModifyRequest;
import com.musicapp.musicservice.dto.response.streaming.TrackDataUploadResponse;
import com.musicapp.musicservice.entity.TrackData;
import com.musicapp.musicservice.gateway.event.TrackDataUploadedEvent;
import com.musicapp.musicservice.integration.stabs.KafkaTestProducer;
import com.musicapp.musicservice.service.TrackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Set;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(IntegrationTestConfig.class)
public class TrackServiceCreationIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TrackService trackService;

    @Autowired
    private KafkaTestProducer kafkaProducer;

    @Value("${track-uploaded-topic}")
    private String trackUploadedTopic;

    @Test
    public void shouldCreateTrackDataAndVerifyDataUpload() {
        TrackDataUploadResponse response = trackService.createTrackData(new TrackDataModifyRequest(
                "Title",
                Set.of()
        ));
        UUID trackId = response.id();
        int duration = 10;

        TrackData trackData = trackService.getTrackDataEntityById(trackId);
        assertEquals("Title", trackData.getTitle());
        assertEquals(0, trackData.getArtists().size());
        assertEquals(0, trackData.getDuration());
        assertFalse(trackData.isValid());

        kafkaProducer.produce(trackUploadedTopic, new TrackDataUploadedEvent(trackId, duration));

        await().atMost(10, java.util.concurrent.TimeUnit.SECONDS).untilAsserted(() -> {
            TrackData newTrackData = trackService.getTrackDataEntityById(trackId);
            assertTrue(newTrackData.isValid());
            assertEquals(duration, newTrackData.getDuration());
        });
    }
}
