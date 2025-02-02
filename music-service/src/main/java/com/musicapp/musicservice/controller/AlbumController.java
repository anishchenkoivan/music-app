package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.AlbumDto;
import com.musicapp.musicservice.dto.request.AlbumModifyRequest;
import com.musicapp.musicservice.dto.request.GetAlbumByTitleRequest;
import com.musicapp.musicservice.dto.response.ApiError;
import com.musicapp.musicservice.service.AlbumService;
import com.musicapp.musicservice.util.AlbumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/music-service/album")
public class AlbumController {
    AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/{id}")
    public AlbumDto getAlbumById(@PathVariable UUID id) {
        return AlbumMapper.toAlbumDto(albumService.getAlbumById(id));
    }

    @PostMapping("/by-title")
    public List<AlbumDto> getAlbumsByTitle(@RequestBody GetAlbumByTitleRequest request) {
        return albumService.getAlbumsByTitle(request.title()).stream().map(AlbumMapper::toAlbumDto).collect(Collectors.toList());
    }

    @PostMapping("/create")
    public UUID createAlbum(@RequestBody AlbumModifyRequest albumData) {
        return albumService.createAlbum(albumData);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> NoSuchElementExceptionHandler(NoSuchElementException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND);
    }
}
