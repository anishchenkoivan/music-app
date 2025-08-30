package com.musicapps.imageservice.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "minio")
data class MinioProps(
    val url: String,
    val accessKey: String,
    val secretKey: String
)
