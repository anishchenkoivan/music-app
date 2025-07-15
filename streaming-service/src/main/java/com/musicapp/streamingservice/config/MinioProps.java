package com.musicapp.streamingservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "minio")
public record MinioProps(
        String url,
        String accessKey,
        String secretKey,
        String bucket) {
}