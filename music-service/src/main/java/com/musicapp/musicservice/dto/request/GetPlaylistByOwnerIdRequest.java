package com.musicapp.musicservice.dto.request;

import java.util.UUID;

public record GetPlaylistByOwnerIdRequest(UUID ownerId) {
}
