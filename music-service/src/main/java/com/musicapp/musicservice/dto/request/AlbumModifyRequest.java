package com.musicapp.musicservice.dto.request;

import com.musicapp.musicservice.dto.TrackViewDto;

import java.util.List;

public record AlbumModifyRequest(String title, String artist, String cover, List<TrackViewDto> tracks) {
}
