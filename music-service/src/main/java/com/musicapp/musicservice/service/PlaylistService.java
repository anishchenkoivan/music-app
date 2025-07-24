package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.dto.request.PlaylistModifyRequest;
import com.musicapp.musicservice.dto.response.PlaylistCreateResponse;
import com.musicapp.musicservice.entity.Playlist;
import com.musicapp.musicservice.entity.PlaylistSpecialType;
import com.musicapp.musicservice.entity.TrackView;
import com.musicapp.musicservice.exception.AccessException;
import com.musicapp.musicservice.repository.PlaylistRepository;
import com.musicapp.musicservice.util.PlaylistFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final PlaylistFactory playlistFactory;
    private final TrackService trackService;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, PlaylistFactory playlistFactory, TrackService trackService) {
        this.playlistRepository = playlistRepository;
        this.playlistFactory = playlistFactory;
        this.trackService = trackService;
    }

    private boolean hasAccess(Playlist playlist, UUID userId) {
        return playlist.isPublic() || playlist.getUserId().equals(userId);
    }

    @Transactional(readOnly = true)
    public PlaylistDto getPlaylistById(UUID playlistId, UUID userId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        if (hasAccess(playlist, userId)) {
            return playlistFactory.toPlaylistDto(playlist);
        }
        throw new AccessException("No access to this playlist");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PlaylistCreateResponse createPlaylist(PlaylistModifyRequest playlistModifyRequest, UUID userId) {
        List<TrackView> tracks = playlistModifyRequest.tracks()
                .stream().map(trackService::getTrackViewEntityById).toList();
        Playlist playlist = playlistFactory.playlist(
                playlistModifyRequest.title(),
                userId,
                playlistModifyRequest.isPublic(),
                tracks
        );
        playlistRepository.save(playlist);
        return new PlaylistCreateResponse(playlist.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePlaylist(UUID playlistId, PlaylistModifyRequest playlistModifyRequest) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        List<TrackView> tracks = playlistModifyRequest.tracks()
                .stream().map(trackService::getTrackViewEntityById).toList();
        playlist.setTitle(playlistModifyRequest.title());
        playlistFactory.applyTracks(playlist, tracks);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addToPlaylist(UUID playlistId, UUID trackId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        playlist.addTrack(trackService.getTrackViewEntityById(trackId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeFromPlaylist(UUID playlistId, UUID trackId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        playlist.removeTrack(trackService.getTrackViewEntityById(trackId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Playlist getUserHistory(UUID userId) {
        Optional<Playlist> historyPlaylist = playlistRepository.findByUserIdAndSpecialType(userId, PlaylistSpecialType.HISTORY);
        if (historyPlaylist.isPresent()) {
            return historyPlaylist.get();
        }
        Playlist newHistoryPlaylist = playlistFactory.specialPlaylist(userId, PlaylistSpecialType.HISTORY);
        playlistRepository.save(newHistoryPlaylist);
        return newHistoryPlaylist;
    }
}
