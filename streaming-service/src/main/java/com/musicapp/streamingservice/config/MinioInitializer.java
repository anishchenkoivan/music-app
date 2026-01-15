package com.musicapp.streamingservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MinioInitializer {
    private static final Logger log = LoggerFactory.getLogger(MinioInitializer.class);
    
    private final MinioClient minioClient;
    private final MinioProps props;

    public MinioInitializer(MinioClient minioClient, MinioProps props) {
        this.minioClient = minioClient;
        this.props = props;
    }

    @PostConstruct
    public void init() throws Exception {
        String bucketName = props.bucket();
        
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
        
        if (!exists) {
            log.info("Bucket '{}' does not exist. Creating...", bucketName);
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            log.info("Bucket '{}' created successfully", bucketName);
        } else {
            log.info("Bucket '{}' already exists", bucketName);
        }
    }
}
