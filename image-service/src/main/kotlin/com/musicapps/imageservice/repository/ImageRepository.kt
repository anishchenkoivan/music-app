package com.musicapps.imageservice.repository

import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

interface ImageRepository {
    fun save(file: MultipartFile, fileName: String)
    fun getData(fileName: String) : InputStream
    fun getStats(fileName: String) : ImageStats
}