package com.musicapp.streamingservice.unit;

import com.musicapp.streamingservice.gateway.UploadKafkaProducer;
import com.musicapp.streamingservice.gateway.event.TrackUploadedEvent;
import com.musicapp.streamingservice.repository.StreamingRepository;
import com.musicapp.streamingservice.service.StreamingService;
import com.musicapp.streamingservice.util.AudioUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest

public class StreamingServiceTest {
    @Mock
    private StreamingRepository streamingRepository;

    @Mock
    private AudioUtil audioUtil;

    @Mock
    private UploadKafkaProducer uploadKafkaProducer;

    @InjectMocks
    private StreamingService streamingService;

    @Test
    public void shouldUploadAudio() {
        String id = UUID.randomUUID().toString();
        int duration = 10;
        when(audioUtil.isMp3File(any())).thenReturn(true);
        when(audioUtil.getMp3Duration(any())).thenReturn((long) duration);

        streamingService.save(null, id);

        verify(streamingRepository, atLeastOnce()).save(any(), eq(id + ".mp3"));
        verify(uploadKafkaProducer, atLeastOnce()).trackUploaded(eq(new TrackUploadedEvent(UUID.fromString(id), duration)));
    }
}
