package com.musicapp.authservice.security;

import com.musicapp.authservice.entity.Role;
import com.musicapp.authservice.entity.User;
import com.musicapp.authservice.exception.TokenInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                .claim("roles", Set.of(Role.USER))
                .expiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
                .signWith(secretKey)
                .compact();
    }

    public boolean isValidToken(String token, User user) {
        return !tokenExpired(token)
                && rolesMatch(token, user.getRoles());
    }

    public UUID getUserId(String token) {
        return UUID.fromString(getClaims(token, Claims::getSubject));
    }

    private boolean tokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    private boolean rolesMatch(String token, Set<Role> rolesFromUser) {
        var rawRoles = getClaims(token, claims -> claims.get("roles", Set.class));
        if (rawRoles == null) {
            return true;
        }

        Set<Role> rolesFromToken = ((Collection<?>) rawRoles).stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(String::toUpperCase)
                .map(Role::valueOf)
                .collect(Collectors.toSet());

        return rolesFromUser.containsAll(rolesFromToken);
    }

    private Date getExpiration(String token) {
        return getClaims(token, Claims::getExpiration);
    }

    private <T> T getClaims(String token, Function<Claims, T> resolver) {
        Claims claims = getAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new TokenInvalidException("Token is invalid");
        }
    }

}
