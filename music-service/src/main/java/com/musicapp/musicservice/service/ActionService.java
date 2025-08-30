package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.response.streaming.StreamingResponse;
import com.musicapp.musicservice.entity.Playlist;
import com.musicapp.musicservice.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActionService {
    private final TokenService tokenService;
    private final PlaylistService playlistService;
    private final TrackService trackService;
    private final StatisticsService statisticsService;

    @Autowired
    public ActionService(TokenService tokenService, PlaylistService playlistService, TrackService trackService, StatisticsService statisticsService) {
        this.tokenService = tokenService;
        this.playlistService = playlistService;
        this.trackService = trackService;
        this.statisticsService = statisticsService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StreamingResponse listen(UUID trackId, UUID userId) {
        String token = tokenService.generateToken(trackId);
        statisticsService.addToHistory(userId, trackId);
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
