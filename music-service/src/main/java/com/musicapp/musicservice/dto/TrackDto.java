package com.musicapp.musicservice.dto;

import java.util.Set;
import java.util.UUID;

public record TrackDto(
        UUID id,
        String title,
        UUID albumId,
        Set<ArtistDto> artists,
        UUID dataId,
        long likesCount,
        long playsCount,
        int duration
) {
}
