package com.musicapps.imageservice.service

import com.musicapps.imageservice.dto.ImageDto
import com.musicapps.imageservice.exception.ImageGetException
import com.musicapps.imageservice.exception.ImageUploadException
import com.musicapps.imageservice.repository.ImageRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(private val repository: ImageRepository) {

    fun save(file: MultipartFile, id: String, type: ImageType = ImageType.GENERAL) {
        require(file.contentType in setOf("image/png", "image/jpeg")) { throw ImageUploadException("Image file type is not allowed") }
        val name = type.name + "_" + id
        repository.save(file, name)
    }

    fun getImage(id: String, type: ImageType = ImageType.GENERAL) : ImageDto {
        val name = type.name + "_" + id
        val bytes = repository.getData(name).use { it.readAllBytes() }
        val type = when(repository.getStats(name).contentType) {
            "image/png" -> MediaType.IMAGE_PNG
            "image/jpeg" -> MediaType.IMAGE_JPEG
            else -> { throw ImageGetException("Failed to determine image type")
            }
        }

        return ImageDto(
            body=bytes,
            type=type
        )
    }
}
