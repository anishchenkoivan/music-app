package com.musicapp.streamingservice.service;

import com.musicapp.streamingservice.dto.AudioStreamingDto;
import com.musicapp.streamingservice.repository.StreamingRepository;
import com.musicapp.streamingservice.util.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;


@Service
public class StreamingService {
    private static final Logger log = LoggerFactory.getLogger(StreamingService.class);
    private final StreamingRepository streamingRepository;

    @Autowired
    public StreamingService(StreamingRepository streamingRepository) {
        this.streamingRepository = streamingRepository;
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
        streamingRepository.save(file, id + ".mp3");
    }
}
