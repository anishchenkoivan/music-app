package com.musicapp.musicservice.dto;

import java.util.List;
import java.util.UUID;

public record PlaylistDto(UUID id, String name, String description, String image, UUID ownerId, boolean isPublic, List<TrackViewDto> tracks) {
}
