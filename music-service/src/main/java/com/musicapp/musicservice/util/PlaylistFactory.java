package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.entity.Playlist;

public class PlaylistFactory {
    public static PlaylistDto toPlaylistDto(Playlist playlist) {
        return new PlaylistDto(
                playlist.getId(),
                playlist.getTitle(),
                playlist.getLength(),
                playlist.getDuration(),
                playlist.isPublic(),
                playlist.getTracks()
                        .stream().map(TrackFactory::toTrackDto).toList()
        );
    }
}
