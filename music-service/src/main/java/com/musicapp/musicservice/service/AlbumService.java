package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.AlbumDto;
import com.musicapp.musicservice.repository.AlbumRepository;
import com.musicapp.musicservice.util.AlbumFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Transactional(readOnly = true)
    public AlbumDto getAlbumById(UUID id) {
        return AlbumFactory.toAlbumDto(albumRepository.findById(id).orElseThrow());
    }
}
