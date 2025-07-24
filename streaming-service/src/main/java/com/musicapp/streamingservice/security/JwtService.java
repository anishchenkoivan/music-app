package com.musicapp.streamingservice.security;

import com.musicapp.streamingservice.dto.UploadDto;
import com.musicapp.streamingservice.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey secretKey;

    @PostConstruct
    private void constructSecretKey() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public UploadDto validateTokenAdnGetUploadDto (String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            String id = claims.getSubject();
            String action = claims.get("action").toString();
            if (!action.equals("UPLOAD")) {
                throw new AuthException("Invalid action");
            }
            return new UploadDto(id);
        } catch (Exception e) {
            throw new AuthException("Token invalid");
        }
    }
}
