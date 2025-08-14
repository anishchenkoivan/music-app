package com.musicapp.musicservice.dto.response.streaming;

import java.util.UUID;

public record TrackDataUploadResponse(UUID id, String uploadToken) {
}
