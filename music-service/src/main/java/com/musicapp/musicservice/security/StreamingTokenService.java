package com.musicapp.musicservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class StreamingTokenService {
    @Value("${hmac.secret}")
    private String secretKey;
    @Value("${streaming.timeout.seconds}")
    private int timeoutSeconds;

    public String generateToken(UUID trackId) {
        Duration validDuration = Duration.ofSeconds(timeoutSeconds);
        long expiry = Instant.now().plus(validDuration).getEpochSecond();
        String payload = trackId + ":" + expiry;

        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(key);
            byte[] signature = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String signatureBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(signature);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes()) + "." + signatureBase64;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }
}
