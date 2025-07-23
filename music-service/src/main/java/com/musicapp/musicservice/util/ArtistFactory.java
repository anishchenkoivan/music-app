package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.dto.request.ArtistModifyRequest;
import com.musicapp.musicservice.entity.Artist;

public class ArtistFactory {
    public static ArtistDto toArtistDto(Artist artist) {
        return new ArtistDto(
                artist.getId(),
                artist.getName()
        );
    }

    public static Artist artist(ArtistModifyRequest artistModifyRequest) {
        Artist artist = new Artist();
        artist.setName(artistModifyRequest.name());
        return artist;
    }
}
