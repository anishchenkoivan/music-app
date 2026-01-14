package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.dto.request.ArtistModifyRequest;
import com.musicapp.musicservice.dto.response.artist.ArtistCreateResponse;
import com.musicapp.musicservice.dto.response.artist.ArtistsListResponse;
import com.musicapp.musicservice.dto.response.user.PublicUserDetailsDtoResponse;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.gateway.UserClient;
import com.musicapp.musicservice.repository.ArtistRepository;
import com.musicapp.musicservice.service.events.ArtistCreatedEvent;
import com.musicapp.musicservice.util.ArtistFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserClient userClient;

    @Autowired
    public ArtistService(
            ArtistRepository artistRepository,
            ApplicationEventPublisher eventPublisher,
            UserClient userClient
    ) {
        this.artistRepository = artistRepository;
        this.eventPublisher = eventPublisher;
        this.userClient = userClient;
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
    protected Artist createArtistEntity(ArtistModifyRequest artistModifyRequest) {
        Artist artist = ArtistFactory.artist(artistModifyRequest);
        artistRepository.save(artist);
        eventPublisher.publishEvent(new ArtistCreatedEvent(artist.getId(), artist.getName()));
        return artist;
    }

    @Transactional
    public ArtistCreateResponse createArtist(ArtistModifyRequest artistModifyRequest) {
        Artist artist = createArtistEntity(artistModifyRequest);
        return new ArtistCreateResponse(artist.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Artist getArtistEntityForUser(UUID userId) {
        Optional<Artist> existingArtist = artistRepository.findByUserId(userId);
        if (existingArtist.isPresent()) {
            return existingArtist.get();
        }
        PublicUserDetailsDtoResponse userDetails = userClient.getUserDetails(userId.toString());
        Artist newArtist = createArtistEntity(new ArtistModifyRequest(userDetails.username()));
        newArtist.setUserId(userId);
        artistRepository.save(newArtist);
        return newArtist;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public ArtistDto getArtistForUser(UUID userId) {
        return ArtistFactory.toArtistDto(getArtistEntityForUser(userId));
    }

    @Transactional(readOnly = true)
    public List<ArtistDto> getArtistsById(List<UUID> ids) {
        return artistRepository.findAllById(ids).stream().map(ArtistFactory::toArtistDto).toList();
    }

    @Transactional(readOnly = true)
    public ArtistsListResponse getAll() {
        return new ArtistsListResponse(
                artistRepository.findAll().stream().map(ArtistFactory::toArtistDto).toList()
        );
    }
}
