package com.musicapp.streamingservice.repository;

import com.musicapp.streamingservice.config.MinioProps;
import com.musicapp.streamingservice.exception.AudioStreamException;
import com.musicapp.streamingservice.exception.AudioSaveException;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Repository
public class MinioStreamingRepository implements StreamingRepository {
    private final MinioClient minioClient;
    private final MinioProps props;

    @Autowired
    public MinioStreamingRepository(MinioClient minioClient, MinioProps props) {
        this.minioClient = minioClient;
        this.props = props;
    }

    @Override
    public InputStream stream(String file, long start, long length) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(props.bucket())
                            .object(file)
                            .offset(start)
                            .length(length)
                            .build()
            );
        } catch (Exception e) {
            throw new AudioStreamException("Failed to get audio file", e);
        }
    }

    @Override
    public void save(MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(props.bucket())
                            .object(file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new AudioSaveException("Failed to save audio file", e);
        }
    }

    @Override
    public long size(String file) {
        try {
            return minioClient.statObject(
                            StatObjectArgs.builder().bucket(props.bucket()).object(file).build())
                    .size();
        } catch (Exception e) {
            throw new AudioStreamException("Failed to get audio file size", e);
        }
    }
}
