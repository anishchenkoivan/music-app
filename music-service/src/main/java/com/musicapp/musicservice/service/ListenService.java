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
public class ListenService {
    private final StreamingTokenService streamingTokenService;
    private final PlaylistService playlistService;

    @Autowired
    public ListenService(StreamingTokenService streamingTokenService, PlaylistService playlistService) {
        this.streamingTokenService = streamingTokenService;
        this.playlistService = playlistService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public StreamingResponse listen(UUID trackId, UUID userId) {
        String token = streamingTokenService.generateToken(trackId);
        Playlist userHistory = playlistService.getUserHistory(userId);
        playlistService.addToPlaylist(userHistory.getId(), trackId);
        return new StreamingResponse(token);
    }

    public void addLike() {

    }

    public void removeLike() {

    }
}
