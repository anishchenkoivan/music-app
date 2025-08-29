package com.musicapps.imageservice.controller

import com.musicapps.imageservice.service.ImageService
import com.musicapps.imageservice.service.ImageType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/images")
class ImageController(private val imageService: ImageService) {
    @PostMapping("/artwork/upload")
    fun uploadArtwork(@RequestParam("file") file: MultipartFile, @RequestParam("id") id: String) {
        imageService.save(file, id, ImageType.ARTWORK)
    }

    @GetMapping("/artwork/{id}")
    fun getArtwork(@PathVariable("id") id: String) : ResponseEntity<ByteArray> {
        val imageDto = imageService.getImage(id, ImageType.ARTWORK)
        return ResponseEntity.ok().contentType(imageDto.type).body(imageDto.body)
    }

    @GetMapping("/test")
    fun test(@RequestParam("str") str: String) : String {
        return str
    }
}
