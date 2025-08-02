package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.TrackDto;
import com.musicapp.musicservice.dto.request.TrackDataModifyRequest;
import com.musicapp.musicservice.dto.request.TrackViewModifyRequest;
import com.musicapp.musicservice.dto.response.TrackDataUploadResponse;
import com.musicapp.musicservice.service.ActionService;
import com.musicapp.musicservice.service.ArtistService;
import com.musicapp.musicservice.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/tracks")
public class TrackController {
    private final TrackService trackService;
    private final ArtistService artistService;
    private final ActionService actionService;

    @Autowired
    public TrackController(TrackService trackService, ArtistService artistService, ActionService actionService) {
        this.trackService = trackService;
        this.artistService = artistService;
        this.actionService = actionService;
    }

    @GetMapping("/{id}")
    public TrackDto getTrackById(@PathVariable("id") UUID id) {
        return trackService.getTrackViewById(id);
    }

    @PostMapping("/upload")
    public TrackDataUploadResponse uploadTrackData(@RequestBody TrackDataModifyRequest request) {
        return trackService.createTrackData(request);
    }

    @PatchMapping("/data/{id}/modify")
    public void modifyTrackData(@PathVariable("id") UUID id, @RequestBody TrackDataModifyRequest request) {
        UUID authenticatedUserArtistId = artistService.getArtistForUser(getAuthenticatedUserId()).id();
        if (trackService.verifyTrackOwnerShip(id, authenticatedUserArtistId)) {
            trackService.updateTrackData(id, request);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/{id}/modify")
    public void modifyTrackView(@PathVariable("id") UUID id, TrackViewModifyRequest request) {
        trackService.updateTrackView(id, request);
    }

    @PostMapping("/{id}/like")
    public void likeTrack(@PathVariable("id") UUID id) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        actionService.addFavorite(id, authenticatedUserId);
    }

    @PostMapping("/{id}/unlike")
    public void unlikeTrack(@PathVariable("id") UUID id) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        actionService.removeFavorite(id, authenticatedUserId);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getPrincipal().toString());
    }
}
