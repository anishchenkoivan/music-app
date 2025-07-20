package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.repository.PlaylistRepository;
import com.musicapp.musicservice.util.PlaylistFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Transactional(readOnly = true)
    public PlaylistDto getPlaylistById(UUID id) {
        return PlaylistFactory.toPlaylistDto(playlistRepository.findById(id).orElseThrow());
    }
}
