package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.TrackDto;
import com.musicapp.musicservice.dto.request.TrackDataModifyRequest;
import com.musicapp.musicservice.dto.request.TrackViewModifyRequest;
import com.musicapp.musicservice.dto.response.artist.ArtistTracksResponse;
import com.musicapp.musicservice.dto.response.streaming.TrackDataUploadResponse;
import com.musicapp.musicservice.entity.Album;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.entity.TrackData;
import com.musicapp.musicservice.entity.TrackView;
import com.musicapp.musicservice.exception.CopyrightException;
import com.musicapp.musicservice.gateway.event.TrackDataUploadedEvent;
import com.musicapp.musicservice.repository.AlbumRepository;
import com.musicapp.musicservice.repository.TrackDataRepository;
import com.musicapp.musicservice.repository.TrackViewRepository;
import com.musicapp.musicservice.security.JwtService;
import com.musicapp.musicservice.util.TrackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TrackService {
    private final TrackViewRepository trackViewRepository;
    private final TrackDataRepository trackDataRepository;
    private final ArtistService artistService;
    private final JwtService jwtService;
    private final TrackFactory trackFactory;
    private final AlbumRepository albumRepository;

    @Autowired
    public TrackService(TrackViewRepository trackViewRepository, TrackDataRepository trackDataRepository, ArtistService artistService, JwtService jwtService, TrackFactory trackFactory, AlbumRepository albumRepository) {
        this.trackViewRepository = trackViewRepository;
        this.trackDataRepository = trackDataRepository;
        this.artistService = artistService;
        this.jwtService = jwtService;
        this.trackFactory = trackFactory;
        this.albumRepository = albumRepository;
    }

    @Transactional(readOnly = true)
    public TrackDto getTrackViewById(UUID id) {
        return trackFactory.toTrackDto(getTrackViewEntityById(id));
    }

    @Transactional(readOnly = true)
    public TrackView getTrackViewEntityById(UUID id) {
        return trackViewRepository.findById(id).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TrackDataUploadResponse createTrackData(TrackDataModifyRequest request) {
        Set<Artist> artists = request.artistIds().stream().map(artistService::getArtistEntityById).collect(Collectors.toSet());
        TrackData trackData = trackFactory.trackData(request.title(), artists);
        trackDataRepository.save(trackData);
        return  new TrackDataUploadResponse(trackData.getId(), jwtService.generateUploadToken(trackData.getId()));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TrackData getTrackDataEntityById(UUID id) {
        return trackDataRepository.findById(id).orElseThrow();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTrackData(UUID id, TrackDataModifyRequest request) {
        TrackData trackData = trackDataRepository.findById(id).orElseThrow();
        trackData.setTitle(request.title());
        List<Album> albumsContainingTrackData = albumRepository.findDistinctByTracks_TrackData(trackData);
        Set<Artist> artists = request.artistIds().stream().map(artistService::getArtistEntityById).collect(Collectors.toSet());
        for (Album album : albumsContainingTrackData) {
            if (!artists.contains(album.getArtist())) {
                throw new CopyrightException("Artist " + album.getArtist() + " must be present");
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTrackView(UUID id, TrackViewModifyRequest request) {
        TrackView trackView = trackViewRepository.findById(id).orElseThrow();
        trackView.setTitle(request.title());
    }

    @Transactional(readOnly = true)
    public boolean verifyTrackOwnerShip(UUID trackDataId, UUID artistId) {
        TrackData trackData = trackDataRepository.findById(trackDataId).orElseThrow();
        return trackData.getArtists().contains(artistService.getArtistEntityById(artistId));
    }

    @Transactional(readOnly = true)
    public ArtistTracksResponse getTracksForArtist(UUID artistId) {
        Artist artist = artistService.getArtistEntityById(artistId);
        return new ArtistTracksResponse(
                trackViewRepository.findByTrackData_ArtistsContains(artist)
                        .stream().map(trackFactory::toTrackDto).toList()
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void trackDataUploaded(TrackDataUploadedEvent event) {
        TrackData trackData = getTrackDataEntityById(event.trackDataId());
        trackData.setValid(true);
        trackData.setDuration(event.duration());
        trackDataRepository.save(trackData);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void changeTrackLikesCount(UUID id, boolean increment) {
        TrackData trackData = trackDataRepository.findByIdForUpdate(id).orElseThrow();
        int difference = increment ? 1 : -1;
        trackData.setLikesCount(trackData.getLikesCount() + difference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void incrementPlayCount(UUID id) {
        TrackData trackData = trackDataRepository.findByIdForUpdate(id).orElseThrow();
        trackData.setLikesCount(trackData.getLikesCount() + 1);
    }

    @Transactional(readOnly = true)
    public List<TrackView> getTrackViewEntitiesById(List<UUID> ids) {
        return trackViewRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<TrackDto> getTrackTrackViewsById(List<UUID> ids) {
        return trackViewRepository.findAllById(ids).stream().map(trackFactory::toTrackDto).toList();
    }
}
