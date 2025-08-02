package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.dto.request.PlaylistModifyRequest;
import com.musicapp.musicservice.dto.response.PlaylistCreateResponse;
import com.musicapp.musicservice.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getPrincipal().toString());
    }

    @GetMapping("/{id}")
    public PlaylistDto getPlaylistById(@PathVariable("id") UUID id) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        return playlistService.getPlaylistById(id, authenticatedUserId);
    }

    @PostMapping("/create")
    public PlaylistCreateResponse createPlaylist(PlaylistModifyRequest playlistModifyRequest) {
        return playlistService.createPlaylist(playlistModifyRequest, getAuthenticatedUserId());
    }

    @PutMapping("/{id}/update")
    public void updatePlaylist(@PathVariable("id") UUID id, PlaylistModifyRequest playlistModifyRequest) {
        playlistService.updatePlaylist(id, playlistModifyRequest);
    }
}
