package com.musicapp.musicservice.dto.request;

import java.util.List;
import java.util.UUID;

public record PlaylistModifyRequest(
        String title,
        boolean isPublic,
        List<UUID> tracks
) {
}
