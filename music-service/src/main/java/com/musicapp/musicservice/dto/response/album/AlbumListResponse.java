package com.musicapp.musicservice.dto.response.album;

import com.musicapp.musicservice.dto.AlbumDto;

import java.util.List;

public record AlbumListResponse(List<AlbumDto> albums) {
}
