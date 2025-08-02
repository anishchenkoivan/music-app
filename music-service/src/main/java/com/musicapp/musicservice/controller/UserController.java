package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.dto.response.PlaylistCollectionResponse;
import com.musicapp.musicservice.service.PlaylistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final PlaylistService playlistService;

    public UserController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getPrincipal().toString());
    }

    @GetMapping("/history")
    public PlaylistDto getUserHistory() {
        UUID authenticatedUserId = getAuthenticatedUserId();
        return playlistService.getUserHistory(authenticatedUserId);
    }

    @GetMapping("/favorites")
    public PlaylistDto getUserFavorites() {
        UUID authenticatedUserId = getAuthenticatedUserId();
        return playlistService.getUserFavorites(authenticatedUserId);
    }

    @GetMapping("/{id}/playlists")
    public PlaylistCollectionResponse getUserPlaylists(@PathVariable UUID id) {
        return null;
    }
}
