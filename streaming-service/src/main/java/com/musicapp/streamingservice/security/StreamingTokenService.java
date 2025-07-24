package com.musicapp.streamingservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class StreamingTokenService {
    @Value("${hmac.secret}")
    private String secretKey;

    public boolean validateToken(String token, String songId) {
        String[] separatedToken = token.split("\\.");
        if (separatedToken.length != 2) {
            return false;
        }

        String payloadEncoded = separatedToken[0];
        String signatureEncoded = separatedToken[1];

        String payload = new String(Base64.getUrlDecoder().decode(payloadEncoded), StandardCharsets.UTF_8);

        try {
            String expectedSignature = generateHmac(payload);
            if (!expectedSignature.equals(signatureEncoded)) {
                return false;
            }

            String[] payloadParts = payload.split(":");
            if (payloadParts.length != 2) return false;

            String encodedSongId = payloadParts[0];
            long expiresAt = Long.parseLong(payloadParts[1]);

            if (!songId.equals(encodedSongId)) return false;
            return Instant.now().getEpochSecond() <= expiresAt;
        } catch (Exception e) {
            return false;
        }
    }

    private String generateHmac(String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(key);
        byte[] signature = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
    }
}
