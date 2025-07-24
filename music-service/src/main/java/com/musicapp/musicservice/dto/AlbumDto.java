package com.musicapp.musicservice.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AlbumDto(
        UUID id,
        String title,
        ArtistDto artist,
        int duration,
        int length,
        LocalDate releaseDate,
        List<TrackDto> tracks
) {
}
