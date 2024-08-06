package com.musicapp.authservice.dto.request;

import java.util.UUID;

public record UserUpdateRequest(UUID id, String password) {
}
