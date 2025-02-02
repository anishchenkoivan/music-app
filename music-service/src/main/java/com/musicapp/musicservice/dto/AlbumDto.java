package com.musicapp.musicservice.dto;

import java.util.List;
import java.util.UUID;

public record AlbumDto(UUID id, String title, String artist, String cover, List<TrackViewDto> tracks) {
}
