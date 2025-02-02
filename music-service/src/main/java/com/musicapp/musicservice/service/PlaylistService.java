package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.request.PlaylistModifyRequest;
import com.musicapp.musicservice.entity.Playlist;

import java.util.List;
import java.util.UUID;

public interface PlaylistService {
    Playlist getPlaylistById(UUID playlistId);
    List<Playlist> getPlaylistsByOwnerId(UUID ownerId);
    List<Playlist> getPlaylistsByName(String playlistName);
    UUID createPlaylist(PlaylistModifyRequest playlistData);
}
