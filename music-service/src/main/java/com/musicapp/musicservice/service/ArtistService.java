package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.dto.request.ArtistModifyRequest;
import com.musicapp.musicservice.dto.response.ArtistCreateResponse;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.repository.ArtistRepository;
import com.musicapp.musicservice.util.ArtistFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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

    @Transactional(propagation =  Propagation.REQUIRED)
    public Artist createArtistEntity(ArtistModifyRequest artistModifyRequest) {
        Artist artist = ArtistFactory.artist(artistModifyRequest);
        artistRepository.save(artist);
        return artist;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public ArtistCreateResponse createArtist(ArtistModifyRequest artistModifyRequest) {
        return new ArtistCreateResponse(createArtistEntity(artistModifyRequest).getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Artist getArtistEntityForUser(UUID userId) {
        Optional<Artist> existingArtist = artistRepository.findByUserId(userId);
        if (existingArtist.isPresent()) {
            return existingArtist.get();
        }
        Artist newArtist = createArtistEntity(new ArtistModifyRequest(userId.toString()));
        newArtist.setUserId(userId);
        artistRepository.save(newArtist);
        return newArtist;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public ArtistDto getArtistForUser(UUID userId) {
        return ArtistFactory.toArtistDto(getArtistEntityForUser(userId));
    }
}
