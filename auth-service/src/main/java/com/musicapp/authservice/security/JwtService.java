package com.musicapp.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration}")
    private long expirationTimeInSeconds;

    private SecretKey secretKey = null;

    @PostConstruct
    private void constructSecretKey() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateToken(UUID userId) {
        long expirationTimeInMillis = expirationTimeInSeconds * 1000;
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
                .signWith(secretKey)
                .compact();
    }

    public boolean isValidToken(String token, UUID userId) {
        return userId.equals(getUserId(token)) && !tokenExpired(token);
    }

    private boolean tokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    private Date getExpiration(String token) {
        return getClaims(token, Claims::getExpiration);
    }

    private UUID getUserId(String token) {
        return UUID.fromString(getClaims(token, Claims::getSubject));
    }

    private <T> T getClaims(String token, Function<Claims, T> resolver) {
        Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
