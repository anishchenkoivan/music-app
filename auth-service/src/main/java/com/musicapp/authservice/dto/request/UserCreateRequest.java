package com.musicapp.authservice.dto.request;

import java.util.UUID;

public record UserCreateRequest(UUID id, String password) {
}
