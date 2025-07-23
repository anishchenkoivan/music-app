package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.TrackDto;
import com.musicapp.musicservice.dto.request.TrackDataModifyRequest;
import com.musicapp.musicservice.dto.response.TrackDataUploadResponse;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.entity.TrackData;
import com.musicapp.musicservice.repository.TrackDataRepository;
import com.musicapp.musicservice.repository.TrackViewRepository;
import com.musicapp.musicservice.security.JwtService;
import com.musicapp.musicservice.util.TrackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public TrackService(TrackViewRepository trackViewRepository, TrackDataRepository trackDataRepository, ArtistService artistService, JwtService jwtService, TrackFactory trackFactory) {
        this.trackViewRepository = trackViewRepository;
        this.trackDataRepository = trackDataRepository;
        this.artistService = artistService;
        this.jwtService = jwtService;
        this.trackFactory = trackFactory;
    }

    @Transactional(readOnly = true)
    public TrackDto getTrackViewById(UUID id) {
        return trackFactory.toTrackDto(trackViewRepository.findById(id).orElseThrow());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TrackDataUploadResponse createTrackData(TrackDataModifyRequest request) {
        Set<Artist> artists = request.artistIds().stream().map(artistService::getArtistEntityById).collect(Collectors.toSet());
        TrackData trackData = trackFactory.trackData(request.title(), artists);
        trackDataRepository.save(trackData);
        return  new TrackDataUploadResponse(jwtService.generateUploadToken(trackData.getId()));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TrackData getTrackDataEntityById(UUID id) {
        return trackDataRepository.findById(id).orElseThrow();
    }
}
