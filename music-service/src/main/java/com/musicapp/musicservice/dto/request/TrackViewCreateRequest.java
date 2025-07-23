package com.musicapp.musicservice.dto.request;

import java.util.UUID;

public record TrackViewCreateRequest(
        String title,
        UUID trackDataId
) {
}
