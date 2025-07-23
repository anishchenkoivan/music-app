package com.musicapp.musicservice.service;

import com.musicapp.musicservice.dto.AlbumDto;
import com.musicapp.musicservice.dto.request.AlbumCreateRequest;
import com.musicapp.musicservice.dto.request.TrackViewCreateRequest;
import com.musicapp.musicservice.dto.response.AlbumCreateResponse;
import com.musicapp.musicservice.entity.Album;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.entity.TrackView;
import com.musicapp.musicservice.exception.CopyrightException;
import com.musicapp.musicservice.repository.AlbumRepository;
import com.musicapp.musicservice.util.AlbumFactory;
import com.musicapp.musicservice.util.TrackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
    private final TrackService trackService;
    private final AlbumFactory albumFactory;
    private final TrackFactory trackFactory;

    @Autowired
    public AlbumService(
            AlbumRepository albumRepository,
            ArtistService artistService,
            TrackService trackService,
            AlbumFactory albumFactory,
            TrackFactory trackFactory
    ) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
        this.trackService = trackService;
        this.albumFactory = albumFactory;
        this.trackFactory = trackFactory;
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
                throw new CopyrightException("Track " + trackView.getTrackData().getTitle() + " does not belong to artist " + artist.getName());
            }
            album.addTrack(trackView);
        }
        albumRepository.save(album);
        return new AlbumCreateResponse(album.getId());
    }
}
