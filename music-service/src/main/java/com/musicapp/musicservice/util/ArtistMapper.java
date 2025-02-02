package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.dto.request.ArtistModifyRequest;
import com.musicapp.musicservice.entity.Artist;

public class ArtistMapper {
    public static Artist toArtist(ArtistModifyRequest artistModifyRequest) {
        return new Artist(
                artistModifyRequest.userId(),
                artistModifyRequest.name(),
                artistModifyRequest.bio()
        );
    }

    public static ArtistDto toArtistDto(Artist artist) {
        return new ArtistDto(
                artist.getId(),
                artist.getUserId(),
                artist.getName(),
                artist.getBio()
        );
    }
}
