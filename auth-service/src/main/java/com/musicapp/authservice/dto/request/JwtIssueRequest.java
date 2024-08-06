package com.musicapp.authservice.dto.request;

import java.util.UUID;

public record JwtIssueRequest(UUID id, String password) {
}
