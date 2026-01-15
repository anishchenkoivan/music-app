package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.AlbumDto;
import com.musicapp.musicservice.dto.request.AlbumCreateRequest;
import com.musicapp.musicservice.dto.request.AlbumGeneralCreateRequest;
import com.musicapp.musicservice.dto.request.TrackViewCreateRequest;
import com.musicapp.musicservice.dto.response.album.AlbumCreateResponse;
import com.musicapp.musicservice.dto.response.album.AlbumListResponse;
import com.musicapp.musicservice.dto.response.artist.ArtistAlbumsResponse;
import com.musicapp.musicservice.entity.Album;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.entity.TrackData;
import com.musicapp.musicservice.entity.TrackView;
import com.musicapp.musicservice.exception.CopyrightException;
import com.musicapp.musicservice.repository.AlbumRepository;
import com.musicapp.musicservice.security.JwtService;
import com.musicapp.musicservice.security.TokenService;
import com.musicapp.musicservice.service.events.AlbumCreatedEvent;
import com.musicapp.musicservice.service.events.TrackViewCreatedEvent;
import com.musicapp.musicservice.util.AlbumFactory;
import com.musicapp.musicservice.util.TrackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
    private final TrackService trackService;
    private final AlbumFactory albumFactory;
    private final TrackFactory trackFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtService jwtService;

    @Autowired
    public AlbumService(
            AlbumRepository albumRepository,
            ArtistService artistService,
            TrackService trackService,
            AlbumFactory albumFactory,
            TrackFactory trackFactory,
            ApplicationEventPublisher eventPublisher,
            JwtService jwtService) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
        this.trackService = trackService;
        this.albumFactory = albumFactory;
        this.trackFactory = trackFactory;
        this.eventPublisher = eventPublisher;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AlbumDto getAlbumById(UUID id) {
        return albumFactory.toAlbumDto(albumRepository.findById(id).orElseThrow());
    }

    @Transactional
    public AlbumCreateResponse createAlbum(AlbumCreateRequest albumCreateRequest) {
        List<TrackView> tracks = albumCreateRequest.generalData().tracks().stream()
                .map((TrackViewCreateRequest trackViewData) -> trackFactory.trackView(
                        trackViewData.title(),
                        trackService.getTrackDataEntityById(trackViewData.trackDataId())

                )).toList();

        Artist artist = artistService.getArtistEntityById(albumCreateRequest.artistId());
        Album album = albumFactory.album(albumCreateRequest.generalData().title(), artist);
        for (TrackView trackView : tracks) {
            if (!trackView.getTrackData().getArtists().contains(artist)) {
                throw new CopyrightException("Track " + trackView.getTrackData().getTitle() +
                        " does not belong to artist " + artist.getName());
            }
            album.addTrack(trackView);
        }
        albumRepository.save(album);

        eventPublisher.publishEvent(new AlbumCreatedEvent(album.getId(), album.getTitle()));
        album.getTracks().forEach(
                track ->
                        eventPublisher.publishEvent(new TrackViewCreatedEvent(track.getId(), track.getTitle()))
        );
        return new AlbumCreateResponse(albumFactory.toAlbumDto(album), jwtService.generateUploadToken(album.getId()));
    }

    @Transactional(readOnly = true)
    public ArtistAlbumsResponse getAlbumsForArtist(UUID artistId) {
        Artist artist = artistService.getArtistEntityById(artistId);
        return new ArtistAlbumsResponse(
                albumRepository.findByArtist(artist)
                        .stream().map(albumFactory::toAlbumDto).toList()
        );
    }

    @Transactional(readOnly = true)
    public List<AlbumDto> getAlbumsById(List<UUID> ids) {
        return albumRepository.findAllById(ids).stream().map(albumFactory::toAlbumDto).toList();
    }

    @Transactional(readOnly = true)
    public AlbumListResponse getAll() {
        return new AlbumListResponse(
                albumRepository.findAll().stream()
                        .map(albumFactory::toAlbumDto)
                        .toList()
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void createSingleFromRawData(TrackData trackData) {
        createAlbum(new AlbumCreateRequest(
                trackData.getArtists().iterator().next().getId(),
                LocalDate.now(),
                new AlbumGeneralCreateRequest(
                        trackData.getTitle(),
                        List.of(new TrackViewCreateRequest(
                                trackData.getTitle(),
                                trackData.getId()
                        ))
                )
        ));
    }
}
