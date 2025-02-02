package com.musicapp.musicservice.service.impl;

import com.musicapp.musicservice.dto.request.AlbumModifyRequest;
import com.musicapp.musicservice.entity.Album;
import com.musicapp.musicservice.repositoy.AlbumRepository;
import com.musicapp.musicservice.service.AlbumService;
import com.musicapp.musicservice.util.AlbumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlbumServiceImpl implements AlbumService {
    AlbumRepository albumRepository;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public Album getAlbumById(UUID albumId) {
        return albumRepository.findById(albumId).orElseThrow();
    }

    @Override
    public List<Album> getAlbumsByTitle(String title) {
        return albumRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public UUID createAlbum(AlbumModifyRequest albumData) {
        Album album = AlbumMapper.toAlbum(albumData);
        return albumRepository.save(album).getId();
    }
}
