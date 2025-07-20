package com.musicapp.musicservice.dto;

import java.util.List;
import java.util.UUID;

public record PlaylistDto(
        UUID id,
        String title,
        int length,
        int duration,
        boolean isPublic,
        List<TrackDto> tracks
) {
}
