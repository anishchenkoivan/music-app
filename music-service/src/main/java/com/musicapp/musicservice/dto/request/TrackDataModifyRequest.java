package com.musicapp.musicservice.dto.request;

import java.util.Set;
import java.util.UUID;

public record TrackDataModifyRequest(String title, Set<UUID> artistIds) {
}
