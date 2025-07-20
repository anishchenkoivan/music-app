package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.TrackDto;
import com.musicapp.musicservice.entity.Artist;
import com.musicapp.musicservice.entity.TrackData;
import com.musicapp.musicservice.entity.TrackView;

import java.util.Set;
import java.util.stream.Collectors;

public class TrackFactory {
    public static TrackDto toTrackDto(TrackView trackView) {
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

    public static TrackData trackData(Set<Artist> artists) {
        TrackData trackData = new TrackData();
        trackData.setArtists(artists);
        return trackData;
    }
}
