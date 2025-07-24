package com.musicapp.streamingservice.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Repository
public interface StreamingRepository {
    InputStream stream(String file, long start, long end);
    void save(MultipartFile file, String fileName);
    long size(String file);
}
