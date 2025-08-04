package com.musicapp.musicservice.dto.response;

import com.musicapp.musicservice.dto.PlaylistDto;

import java.util.List;

public record PlaylistCollectionResponse(List<PlaylistDto> playlists) {
}
