package com.musicapp.musicservice.dto.response.album;

import com.musicapp.musicservice.dto.AlbumDto;

public record AlbumCreateResponse(AlbumDto album, String imageUploadToken) {
}
