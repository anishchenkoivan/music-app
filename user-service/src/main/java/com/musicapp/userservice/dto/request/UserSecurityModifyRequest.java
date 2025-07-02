package com.musicapp.userservice.dto.request;

import java.util.UUID;

public record UserSecurityModifyRequest(UUID id, String password) {
}
