package com.musicapps.imageservice.exception

class ImageUploadException : RuntimeException {
    constructor(message: String?, cause: Throwable) : super(message, cause)
    constructor(message: String?) : super(message)
}