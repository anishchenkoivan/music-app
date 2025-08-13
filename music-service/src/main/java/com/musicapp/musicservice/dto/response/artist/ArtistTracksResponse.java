package com.musicapp.musicservice.dto.response.artist;

import com.musicapp.musicservice.dto.TrackDto;

import java.util.List;

public record ArtistTracksResponse(List<TrackDto> artistTracksList) {
}
