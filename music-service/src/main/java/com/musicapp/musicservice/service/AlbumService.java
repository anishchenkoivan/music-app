package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.request.AlbumModifyRequest;
import com.musicapp.musicservice.entity.Album;

import java.util.List;
import java.util.UUID;

public interface AlbumService {
    Album getAlbumById(UUID albumId);
    List<Album> getAlbumsByTitle(String title);
    UUID createAlbum(AlbumModifyRequest albumData);
}
