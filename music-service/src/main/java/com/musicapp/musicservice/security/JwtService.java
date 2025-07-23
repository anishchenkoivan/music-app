package com.musicapp.musicservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey secretKey;

    @PostConstruct
    private void constructSecretKey() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateUploadToken(UUID trackDataId) {
        return Jwts.builder()
                .subject(trackDataId.toString())
                .claim("actions", TrackActions.UPLOAD)
                .signWith(secretKey)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parse(token);
        } catch (RuntimeException exception) {
            return false;
        }
        return true;
    }
}
