package com.musicapps.imageservice.security

import com.musicapps.imageservice.dto.UploadDto
import com.musicapps.imageservice.exception.AuthException
import io.jsonwebtoken.Jwts
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.crypto.SecretKey

@Service
class JwtService(@param:Value($$"${jwt.secret}") private val secretKeyString: String) {
    private lateinit var secretKey: SecretKey

    @PostConstruct
    private fun constructSecretKey() {

    }

    fun validateTokenAndGetUploadDto(token: String): UploadDto {
        try {
            val claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
            val id = claims.subject
            val action = claims["action"].toString()
            if (!action.contentEquals("UPLOAD")) {
                throw AuthException("Invalid action")
            }
            return UploadDto(id)
        } catch (e: Exception) {
            throw AuthException("Invalid token")
        }
    }
}