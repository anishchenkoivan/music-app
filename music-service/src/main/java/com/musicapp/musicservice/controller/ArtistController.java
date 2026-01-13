package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.dto.response.artist.ArtistAlbumsResponse;
import com.musicapp.musicservice.dto.response.artist.ArtistTracksResponse;
import com.musicapp.musicservice.dto.response.artist.ArtistsListResponse;
import com.musicapp.musicservice.service.AlbumService;
import com.musicapp.musicservice.service.ArtistService;
import com.musicapp.musicservice.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/artists")
public class ArtistController {
    private final ArtistService artistService;
    private final TrackService trackService;
    private final AlbumService albumService;

    @Autowired
    public ArtistController(ArtistService artistService, TrackService trackService, AlbumService albumService) {
        this.artistService = artistService;
        this.trackService = trackService;
        this.albumService = albumService;
    }

    @GetMapping("/{id}")
    ArtistDto getArtistById(@PathVariable("id") UUID id) {
        return artistService.getArtistById(id);
    }

    @GetMapping("/user/{userId}")
    ArtistDto getArtistForUser(@PathVariable("userId") UUID userId) {
        return artistService.getArtistForUser(userId);
    }

    @GetMapping("/{id}/tracks")
    ArtistTracksResponse getArtistTracks(@PathVariable("id") UUID id) {
        return trackService.getTracksForArtist(id);
    }

    @GetMapping("/{id}/albums")
    ArtistAlbumsResponse getArtistAlbums(@PathVariable("id") UUID id) {
        return albumService.getAlbumsForArtist(id);
    }

    @GetMapping("/all")
    ArtistsListResponse getAll() {
        return artistService.getAll();
    }
}
