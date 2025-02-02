package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.request.ArtistModifyRequest;
import com.musicapp.musicservice.entity.Artist;

import java.util.List;
import java.util.UUID;

public interface ArtistService {
    Artist getArtistById(UUID artistId);
    List<Artist> getArtistsByName(String artistName);
    UUID createArtist(ArtistModifyRequest artistData);
}
