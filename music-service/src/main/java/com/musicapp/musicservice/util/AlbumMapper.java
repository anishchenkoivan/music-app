package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.request.AlbumModifyRequest;
import com.musicapp.musicservice.entity.Album;
import com.musicapp.musicservice.dto.AlbumDto;

public class AlbumMapper {
    public static Album toAlbum(AlbumModifyRequest albumModifyRequest) {
        return new Album(
                albumModifyRequest.title(),
                albumModifyRequest.artist(),
                albumModifyRequest.cover(),
                albumModifyRequest.tracks().stream().map(TrackViewMapper::toTrackView).toList()
        );
    }

    public static AlbumDto toAlbumDto(Album album) {
        return new AlbumDto(
                album.getId(),
                album.getTitle(),
                album.getArtist(),
                album.getCover(),
                album.getTracks().stream().map(TrackViewMapper::toTrackViewDto).toList()
        );
    }
}
