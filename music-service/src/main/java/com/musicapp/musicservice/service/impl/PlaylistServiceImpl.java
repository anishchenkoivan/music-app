package com.musicapp.musicservice.service.impl;

import com.musicapp.musicservice.dto.request.PlaylistModifyRequest;
import com.musicapp.musicservice.entity.Playlist;
import com.musicapp.musicservice.repositoy.PlaylistRepository;
import com.musicapp.musicservice.service.PlaylistService;
import com.musicapp.musicservice.util.PlaylistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlaylistServiceImpl implements PlaylistService {
    PlaylistRepository playlistRepository;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Override
    public Playlist getPlaylistById(UUID playlistId) {
        return playlistRepository.findById(playlistId).orElseThrow();
    }

    @Override
    public List<Playlist> getPlaylistsByOwnerId(UUID ownerId) {
        return playlistRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Playlist> getPlaylistsByName(String playlistName) {
        return playlistRepository.findByNameContainingIgnoreCase(playlistName);
    }

    @Override
    public UUID createPlaylist(PlaylistModifyRequest playlistData) {
        Playlist playlist = PlaylistMapper.toPlaylist(playlistData);
        return playlistRepository.save(playlist).getId();
    }
}
