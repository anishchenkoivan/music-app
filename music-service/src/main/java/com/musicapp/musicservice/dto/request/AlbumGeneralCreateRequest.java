package com.musicapp.musicservice.dto.request;

import java.util.List;

public record AlbumGeneralCreateRequest(
        String title,
        List<TrackViewCreateRequest> tracks
) {
}
