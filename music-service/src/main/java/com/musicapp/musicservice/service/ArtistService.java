package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.repository.ArtistRepository;
import com.musicapp.musicservice.util.ArtistFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ArtistService {
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional(readOnly = true)
    public ArtistDto getArtistById(UUID id) {
        return ArtistFactory.toArtistDto(artistRepository.findById(id).orElseThrow());
    }

    @Transactional(readOnly = true)
    public Artist getArtistEntityById(UUID id) {
        return artistRepository.findById(id).orElseThrow();
    }
}
