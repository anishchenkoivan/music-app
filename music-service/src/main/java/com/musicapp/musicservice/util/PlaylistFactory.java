package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.entity.Playlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
}
