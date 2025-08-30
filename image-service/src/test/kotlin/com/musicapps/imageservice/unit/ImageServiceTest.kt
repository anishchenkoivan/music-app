package com.musicapps.imageservice.unit

import com.musicapps.imageservice.exception.ImageUploadException
import com.musicapps.imageservice.repository.ImageRepository
import com.musicapps.imageservice.repository.ImageStats
import com.musicapps.imageservice.service.ImageService
import com.musicapps.imageservice.service.ImageType
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ImageServiceTest() {
    private val imageRepository = mockk<ImageRepository>()
    private val imageService = ImageService(imageRepository)

    @Test
    fun shouldSaveAndReturnArtwork() {
        every { imageRepository.save(any(), any()) } returns Unit
        val file = mockk<MultipartFile>()
        every { file.contentType } returns "image/png"
        val id = "123"
        val expectedSavedName = ImageType.ARTWORK.name + "_" + id
        every { imageRepository.getData(expectedSavedName) } returns InputStream.nullInputStream()
        every { imageRepository.getStats(expectedSavedName) } returns ImageStats("image/png")

        assertDoesNotThrow { imageService.save(file, id, ImageType.ARTWORK) }

        val imageDto = imageService.getImage(id, ImageType.ARTWORK)
        assertEquals("image/png", imageDto.type.toString())
    }

    @Test
    fun shouldFailToSaveUnsupportedImageType() {
        val file = mockk<MultipartFile>()
        every { file.contentType } returns "image/gif"

        assertThrows<ImageUploadException> { imageService.save(file, "123", ImageType.ARTWORK) }
    }
}
