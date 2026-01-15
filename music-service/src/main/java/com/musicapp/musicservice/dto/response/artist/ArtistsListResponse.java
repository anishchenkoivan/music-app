package com.musicapp.musicservice.dto.response.artist;

import com.musicapp.musicservice.dto.ArtistDto;

import java.util.List;

public record ArtistsListResponse(List<ArtistDto> artists) {
}
