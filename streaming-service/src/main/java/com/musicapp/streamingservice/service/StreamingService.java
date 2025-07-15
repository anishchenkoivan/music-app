package com.musicapp.streamingservice.service;

import com.musicapp.streamingservice.dto.AudioStreamingDto;
import com.musicapp.streamingservice.exception.AudioStreamException;
import com.musicapp.streamingservice.repository.StreamingRepository;
import com.musicapp.streamingservice.util.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;


@Service
public class StreamingService {
    private final StreamingRepository streamingRepository;

    @Autowired
    public StreamingService(StreamingRepository streamingRepository) {
        this.streamingRepository = streamingRepository;
    }

    public AudioStreamingDto stream(String id, String requestedRange) {
        String fileName = id + ".mp3";
        System.out.println(fileName);
        long fileSize = streamingRepository.size(fileName);
        Range range = Range.parse(requestedRange, fileSize);
        StreamingResponseBody body;
        try (InputStream objectStream = streamingRepository.stream(fileName, range.start(), range.end())) {
            body = objectStream::transferTo;
        } catch (IOException e) {
            throw new AudioStreamException("Failed to stream audio file", e);
        }

        return new AudioStreamingDto(range, body);
    }

    public void save(MultipartFile file) {
        streamingRepository.save(file);
    }
}
