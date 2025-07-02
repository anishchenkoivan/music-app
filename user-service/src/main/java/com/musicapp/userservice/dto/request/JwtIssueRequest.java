package com.musicapp.userservice.dto.request;

import java.util.UUID;

public record JwtIssueRequest(UUID id, String password) {
}

