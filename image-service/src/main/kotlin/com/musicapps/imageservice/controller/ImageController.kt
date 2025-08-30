package com.musicapps.imageservice.controller

import com.musicapps.imageservice.dto.error.ErrorResponse
import com.musicapps.imageservice.exception.AuthException
import com.musicapps.imageservice.security.JwtService
import com.musicapps.imageservice.service.ImageService
import com.musicapps.imageservice.service.ImageType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/images")
class ImageController(private val imageService: ImageService, private val jwtService: JwtService) {
    @PostMapping("/artwork/upload")
    fun uploadArtwork(@RequestParam("file") file: MultipartFile, @RequestHeader headers: HttpHeaders) {
        val header = headers.getFirst(HttpHeaders.AUTHORIZATION)
        requireNotNull(header) { throw ResponseStatusException(HttpStatus.UNAUTHORIZED) }

        val token = header.substring(7)
        val uploadDto = jwtService.validateTokenAndGetUploadDto(token)
        imageService.save(file, uploadDto.id, ImageType.ARTWORK)
    }

    @GetMapping("/artwork/{id}")
    fun getArtwork(@PathVariable("id") id: String) : ResponseEntity<ByteArray> {
        val imageDto = imageService.getImage(id, ImageType.ARTWORK)
        return ResponseEntity.ok().contentType(imageDto.type).body(imageDto.body)
    }

    @ExceptionHandler(AuthException::class)
    fun authExceptionHandler(ex: AuthException) : ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(ex.message ?: "Authentication error"),
            HttpStatus.UNAUTHORIZED
        )
}
