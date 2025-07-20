package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.entity.Artist;

public class ArtistFactory {
    public static ArtistDto toArtistDto(Artist artist) {
        return new ArtistDto(
                artist.getId(),
                artist.getName()
        );
    }
}
