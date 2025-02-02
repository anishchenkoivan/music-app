package com.musicapp.musicservice.dto.request;

import com.musicapp.musicservice.dto.TrackViewDto;

import java.util.List;
import java.util.UUID;

public record ArtistModifyRequest(UUID userId, String name, String bio, List<TrackViewDto> tracks) {
}
