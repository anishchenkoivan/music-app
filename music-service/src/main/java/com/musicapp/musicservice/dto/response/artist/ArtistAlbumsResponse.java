package com.musicapp.musicservice.dto.response.artist;

import com.musicapp.musicservice.dto.AlbumDto;

import java.util.List;

public record ArtistAlbumsResponse(List<AlbumDto> albums) {
}
