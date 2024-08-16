package com.musicapp.mainservice.dto;

import java.util.UUID;

public record AuthDto(UUID id, String token) {
}
