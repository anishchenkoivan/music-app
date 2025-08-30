package com.musicapps.imageservice.dto

import org.springframework.http.MediaType

data class ImageDto(val body: ByteArray, val type: MediaType)
