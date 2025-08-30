package com.musicapps.imageservice.repository

import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.StatObjectArgs
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

@Repository
class MinioImageRepository(private val minioClient: MinioClient) : ImageRepository {
    override fun save(
        file: MultipartFile,
        fileName: String
    ) {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket("images")
                .`object`(fileName)
                .stream(file.inputStream, file.size, -1)
                .contentType(file.contentType)
                .build()
        )
    }

    override fun getData(fileName: String) : InputStream {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket("images")
                .`object`(fileName)
                .build()
        )
    }

    override fun getStats(fileName: String) : ImageStats {
        val minioStats =  minioClient.statObject(
            StatObjectArgs.builder()
                .bucket("images")
                .`object`(fileName)
                .build()
        )
        return ImageStats(minioStats.contentType())
    }
}