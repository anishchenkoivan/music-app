package com.musicapp.musicservice.dto.response.auth;

import java.util.List;
import java.util.UUID;

public record TokenValidateResponse(UUID id, List<String> roles) {
}
