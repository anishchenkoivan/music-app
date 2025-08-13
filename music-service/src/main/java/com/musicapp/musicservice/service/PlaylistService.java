package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.dto.request.PlaylistModifyRequest;
import com.musicapp.musicservice.dto.response.playlist.PlaylistCreateResponse;
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
        List<TrackView> tracks = trackService.getTrackViewEntitiesById(playlistModifyRequest.tracks());
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
        List<TrackView> tracks = trackService.getTrackViewEntitiesById(playlistModifyRequest.tracks());
        playlist.setTitle(playlistModifyRequest.title());
        playlistFactory.applyTracks(playlist, tracks);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addToPlaylist(UUID playlistId, UUID trackId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        return playlist.addTrack(trackService.getTrackViewEntityById(trackId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeFromPlaylist(UUID playlistId, UUID trackId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow();
        return playlist.removeTrack(trackService.getTrackViewEntityById(trackId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Playlist getUserFavoritesEntity(UUID userId) {
        Optional<Playlist> likesPlaylist = playlistRepository.findByUserIdAndSpecialType(userId, PlaylistSpecialType.FAVORITE);
        if (likesPlaylist.isPresent()) {
            return likesPlaylist.get();
        }
        Playlist newFavoritesPlaylist = playlistFactory.specialPlaylist(userId, PlaylistSpecialType.FAVORITE);
        playlistRepository.save(newFavoritesPlaylist);
        return newFavoritesPlaylist;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PlaylistDto getUserFavorites(UUID userId) {
        return playlistFactory.toPlaylistDto(getUserFavoritesEntity(userId));
    }
}
