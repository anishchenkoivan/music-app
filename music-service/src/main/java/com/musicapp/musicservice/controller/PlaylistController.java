package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.PlaylistDto;
import com.musicapp.musicservice.dto.request.GetPlaylistByNameRequest;
import com.musicapp.musicservice.dto.request.GetPlaylistByOwnerIdRequest;
import com.musicapp.musicservice.dto.request.PlaylistModifyRequest;
import com.musicapp.musicservice.dto.response.ApiError;
import com.musicapp.musicservice.service.PlaylistService;
import com.musicapp.musicservice.util.PlaylistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/music-service/playlist")
public class PlaylistController {
    PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/{id}")
    public PlaylistDto getPlaylistById(@PathVariable UUID id) {
        return PlaylistMapper.toPlaylistDto(playlistService.getPlaylistById(id));
    }

    @PostMapping("/by-owner")
    public List<PlaylistDto> getPlaylistsByOwnerId(@RequestBody GetPlaylistByOwnerIdRequest request) {
        return playlistService.getPlaylistsByOwnerId(request.ownerId()).stream().map(PlaylistMapper::toPlaylistDto).toList();
    }

    @PostMapping("/by-name")
    public List<PlaylistDto> getPlaylistsByName(@RequestBody GetPlaylistByNameRequest request) {
        return playlistService.getPlaylistsByName(request.name()).stream().map(PlaylistMapper::toPlaylistDto).toList();
    }

    @PostMapping("/create")
    public UUID createPlaylist(PlaylistModifyRequest playlistData) {
        return playlistService.createPlaylist(playlistData);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> NoSuchElementExceptionHandler(NoSuchElementException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND);
    }
}
