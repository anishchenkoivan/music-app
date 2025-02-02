package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.dto.request.PlaylistModifyRequest;
import com.musicapp.musicservice.entity.Playlist;

public class PlaylistMapper {
    public static Playlist toPlaylist(PlaylistModifyRequest playlistModifyRequest) {
        return new Playlist(
                playlistModifyRequest.name(),
                playlistModifyRequest.description(),
                playlistModifyRequest.image(),
                playlistModifyRequest.ownerId(),
                playlistModifyRequest.isPublic(),
                playlistModifyRequest.tracks().stream().map(TrackViewMapper::toTrackView).toList()
        );
    }

    public static PlaylistDto toPlaylistDto(Playlist playlist) {
        return new PlaylistDto(
                playlist.getId(),
                playlist.getName(),
                playlist.getDescription(),
                playlist.getImage(),
                playlist.getOwnerId(),
                playlist.isPublic(),
                playlist.getTracks().stream().map(TrackViewMapper::toTrackViewDto).toList()
        );
    }
}
