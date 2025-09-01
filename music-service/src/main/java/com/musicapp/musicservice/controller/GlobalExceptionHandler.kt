package com.musicapp.musicservice.controller

import com.musicapp.musicservice.dto.response.error.ErrorResponse
import com.musicapp.musicservice.exception.AccessException
import com.musicapp.musicservice.exception.CopyrightException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(AccessException::class)
    fun accessExceptionHandler(ex: AccessException) =
        ResponseEntity(
            ErrorResponse(ex.message ?: "Access denied"),
            HttpStatus.FORBIDDEN
        )

    @ExceptionHandler(CopyrightException::class)
    fun copyrightExceptionHandler(ex: CopyrightException) =
        ResponseEntity(
        ErrorResponse(ex.message ?: "Copyright error"),
        HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(NoSuchElementException::class)
    fun noSuchItemExceptionHandler(ex: NoSuchElementException) =
        ResponseEntity(
            ErrorResponse(ex.message ?: "Not found"),
            HttpStatus.NOT_FOUND
        )
}
