package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.TrackDto;
import com.musicapp.musicservice.dto.request.TrackViewCreateRequest;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.entity.TrackData;
import com.musicapp.musicservice.entity.TrackView;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TrackFactory {
    @Transactional(propagation = Propagation.SUPPORTS)
    public TrackDto toTrackDto(TrackView trackView) {
        return new TrackDto(
                trackView.getId(),
                trackView.getTitle(),
                trackView.getAlbum().getId(),
                trackView.getTrackData().getArtists()
                        .stream().map(ArtistFactory::toArtistDto).collect(Collectors.toSet()),
                trackView.getTrackData().getId(),
                trackView.getTrackData().getLikesCount(),
                trackView.getTrackData().getPlaysCount(),
                trackView.getTrackData().getDuration()
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TrackData trackData(String title, Set<Artist> artists) {
        TrackData trackData = new TrackData();
        trackData.setTitle(title);
        trackData.setArtists(artists);
        return trackData;
    }

    public TrackView trackView(String title, TrackData trackData) {
        TrackView trackView = new TrackView();
        trackView.setTitle(title);
        trackView.setTrackData(trackData);
        return trackView;
    }
}
