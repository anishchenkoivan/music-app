package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.TrackDto;
import com.musicapp.musicservice.dto.request.TrackDataModifyRequest;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.entity.TrackData;
import com.musicapp.musicservice.repository.TrackDataRepository;
import com.musicapp.musicservice.repository.TrackViewRepository;
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

    @Autowired
    public TrackService(TrackViewRepository trackViewRepository, TrackDataRepository trackDataRepository, ArtistService artistService) {
        this.trackViewRepository = trackViewRepository;
        this.trackDataRepository = trackDataRepository;
        this.artistService = artistService;
    }

    @Transactional(readOnly = true)
    public TrackDto getTrackViewById(UUID id) {
        return TrackFactory.toTrackDto(trackViewRepository.findById(id).orElseThrow());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String createTrackData(TrackDataModifyRequest request) {
        Set<Artist> artists = request.artistIds().stream().map(artistService::getArtistEntityById).collect(Collectors.toSet());
        TrackData trackData = TrackFactory.trackData(artists);
        trackDataRepository.save(trackData);
        return trackData.getId().toString();
    }
}
