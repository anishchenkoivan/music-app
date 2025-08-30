package com.musicapps.imageservice.config

import io.minio.MinioClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MinioProps::class)
class MinioConfig {
    @Bean
    fun minio(props: MinioProps): MinioClient {
        return MinioClient.builder()
            .endpoint(props.url)
            .credentials(props.accessKey, props.secretKey)
            .build()
    }
}