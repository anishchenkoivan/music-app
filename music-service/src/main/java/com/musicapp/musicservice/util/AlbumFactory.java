package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.AlbumDto;
import com.musicapp.musicservice.entity.Album;

public class AlbumFactory {
    public static AlbumDto toAlbumDto(Album album) {
        return new AlbumDto(
                album.getId(),
                album.getTitle(),
                ArtistFactory.toArtistDto(album.getArtist()),
                album.getDuration(),
                album.getLength(),
                album.getReleaseDate(),
                album.getTracks()
                        .stream().map(TrackFactory::toTrackDto).toList()
        );
    }
}
