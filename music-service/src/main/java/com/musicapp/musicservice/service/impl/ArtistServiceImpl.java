package com.musicapp.musicservice.service.impl;

import com.musicapp.musicservice.dto.request.ArtistModifyRequest;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.repositoy.ArtistRepository;
import com.musicapp.musicservice.service.ArtistService;
import com.musicapp.musicservice.util.ArtistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ArtistServiceImpl implements ArtistService {
    ArtistRepository artistRepository;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public Artist getArtistById(UUID artistId) {
        return artistRepository.findById(artistId).orElseThrow();
    }

    @Override
    public List<Artist> getArtistsByName(String artistName) {
        return artistRepository.findByNameContainingIgnoreCase(artistName);
    }

    @Override
    public UUID createArtist(ArtistModifyRequest artistData) {
        Artist artist = ArtistMapper.toArtist(artistData);
        return artistRepository.save(artist).getId();
    }
}
