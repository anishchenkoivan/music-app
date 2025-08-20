package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.dto.response.playlist.PlaylistCollectionResponse;
import com.musicapp.musicservice.dto.response.statistics.UserHistoryResponse;
import com.musicapp.musicservice.service.PlaylistService;
import com.musicapp.musicservice.service.StatisticsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/music")
public class UserController {

    private final PlaylistService playlistService;
    private final StatisticsService statisticsService;

    public UserController(PlaylistService playlistService, StatisticsService statisticsService) {
        this.playlistService = playlistService;
        this.statisticsService = statisticsService;
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(authentication.getPrincipal().toString());
    }

    @GetMapping("/history")
    public UserHistoryResponse getUserHistory(@RequestParam Integer limit) {
        UUID authenticatedUserId = getAuthenticatedUserId();
        return statisticsService.getUserHistory(authenticatedUserId, limit);
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
