package com.musicapp.streamingservice.service;

import com.musicapp.streamingservice.dto.AudioStreamingDto;
import com.musicapp.streamingservice.gateway.UploadKafkaProducer;
import com.musicapp.streamingservice.gateway.event.TrackUploadedEvent;
import com.musicapp.streamingservice.repository.StreamingRepository;
import com.musicapp.streamingservice.util.AudioUtil;
import com.musicapp.streamingservice.util.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


@Service
public class StreamingService {
    private final StreamingRepository streamingRepository;
    private final AudioUtil audioUtil;
    private final UploadKafkaProducer uploadKafkaProducer;

    @Autowired
    public StreamingService(
            StreamingRepository streamingRepository,
            AudioUtil audioUtil,
            UploadKafkaProducer uploadKafkaProducer
            ) {
        this.streamingRepository = streamingRepository;
        this.audioUtil = audioUtil;
        this.uploadKafkaProducer = uploadKafkaProducer;
    }

    public AudioStreamingDto stream(String id, String requestedRange) {
        String fileName = id + ".mp3";
        long fileSize = streamingRepository.size(fileName);
        Range range = Range.parse(requestedRange, fileSize);
        InputStream objectStream = streamingRepository.stream(fileName, range.start(), range.end());

        StreamingResponseBody body = outputStream -> {
            try {
                objectStream.transferTo(outputStream);
            } catch (IOException ignored) {}
        };

        return new AudioStreamingDto(range, body);
    }

    public void save(MultipartFile file, String id) {
        if (!audioUtil.isMp3File(file)) {
            throw new IllegalArgumentException("File is not an mp3 file");
        }
        TrackUploadedEvent event = new TrackUploadedEvent(UUID.fromString(id), (int) audioUtil.getMp3Duration(file));
        streamingRepository.save(file, id + ".mp3");
        uploadKafkaProducer.trackUploaded(event);
    }
}
