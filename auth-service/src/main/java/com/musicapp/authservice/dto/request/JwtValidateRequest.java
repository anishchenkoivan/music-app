package com.musicapp.authservice.dto.request;

import java.util.UUID;

public record JwtValidateRequest(UUID userId, String token) {
}
