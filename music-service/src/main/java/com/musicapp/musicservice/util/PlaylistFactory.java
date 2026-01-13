package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.entity.Playlist;
import com.musicapp.musicservice.entity.PlaylistSpecialType;
import com.musicapp.musicservice.entity.TrackView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PlaylistFactory {
    private final TrackFactory trackFactory;

    @Autowired
    public PlaylistFactory(TrackFactory trackFactory) {
        this.trackFactory = trackFactory;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PlaylistDto toPlaylistDto(Playlist playlist) {
        return new PlaylistDto(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getLength(),
                playlist.getDuration(),
                playlist.isPublic(),
                playlist.getTracks()
                        .stream().map(trackFactory::toTrackDto).toList()
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Playlist playlist(String title, UUID userId, boolean isPublic, List<TrackView> tracks) {
        Playlist playlist = new Playlist();
        playlist.setUserId(userId);
        playlist.setTitle(title);
        playlist.setPublic(isPublic);
        for (TrackView track : tracks) {
            playlist.addTrack(track);
        }
        return playlist;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void applyTracks(Playlist playlist, List<TrackView> tracks) {
        playlist.setTracks(new ArrayList<>());
        playlist.setDuration(0);
        playlist.setLength(0);
        for (TrackView track : tracks) {
            playlist.addTrack(track);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Playlist specialPlaylist(UUID userId, PlaylistSpecialType type) {
        Playlist playlist = new Playlist();
        playlist.setPublic(false);
        playlist.setSpecial(true);
        playlist.setUserId(userId);
        playlist.setSpecialType(type);
        return playlist;
    }
}
