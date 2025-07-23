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
    private final PlaylistFactory playlistFactory;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, PlaylistFactory playlistFactory) {
        this.playlistRepository = playlistRepository;
        this.playlistFactory = playlistFactory;
    }

    @Transactional(readOnly = true)
    public PlaylistDto getPlaylistById(UUID id) {
        return playlistFactory.toPlaylistDto(playlistRepository.findById(id).orElseThrow());
    }
}
