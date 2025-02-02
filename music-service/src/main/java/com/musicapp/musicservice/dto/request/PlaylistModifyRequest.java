package com.musicapp.musicservice.dto.request;

import com.musicapp.musicservice.dto.TrackViewDto;

import java.util.List;
import java.util.UUID;

public record PlaylistModifyRequest(String name, String description, String image, UUID ownerId, boolean isPublic, List<TrackViewDto> tracks) {
}
