package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.response.StreamingResponse;
import com.musicapp.musicservice.entity.Playlist;
import com.musicapp.musicservice.security.StreamingTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActionService {
    private final StreamingTokenService streamingTokenService;
    private final PlaylistService playlistService;
    private final TrackService trackService;

    @Autowired
    public ActionService(StreamingTokenService streamingTokenService, PlaylistService playlistService, TrackService trackService) {
        this.streamingTokenService = streamingTokenService;
        this.playlistService = playlistService;
        this.trackService = trackService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StreamingResponse listen(UUID trackId, UUID userId) {
        String token = streamingTokenService.generateToken(trackId);
        Playlist userHistory = playlistService.getUserHistoryEntity(userId);
        playlistService.addToPlaylist(userHistory.getId(), trackId);
        trackService.incrementPlayCount(trackId);
        return new StreamingResponse(token);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addFavorite(UUID trackId, UUID userId) {
        Playlist userFavorites = playlistService.getUserFavoritesEntity(userId);
        if (playlistService.addToPlaylist(userFavorites.getId(), trackId)) {
            trackService.changeTrackLikesCount(trackId, true);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeFavorite(UUID trackId, UUID userId) {
        Playlist userFavorites = playlistService.getUserFavoritesEntity(userId);
        if (playlistService.removeFromPlaylist(userFavorites.getId(), trackId)) {
            trackService.changeTrackLikesCount(trackId, false);
        }
    }
}
