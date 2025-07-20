package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/{id}")
    PlaylistDto getPlaylistById(@PathVariable("id") UUID id) {
        return playlistService.getPlaylistById(id);
    }
}
