package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.AlbumDto;
import com.musicapp.musicservice.dto.request.AlbumCreateRequest;
import com.musicapp.musicservice.dto.request.AlbumGeneralCreateRequest;
import com.musicapp.musicservice.dto.response.AlbumCreateResponse;
import com.musicapp.musicservice.dto.response.AlbumsSearchResponse;
import com.musicapp.musicservice.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("/albums")
public class AlbumController {
    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/{id}")
    AlbumDto getAlbumById(@PathVariable("id") UUID id) {
        return albumService.getAlbumById(id);
    }

    @PostMapping("/create")
    AlbumCreateResponse createAlbum(@RequestBody AlbumGeneralCreateRequest request) {
        return albumService.createAlbum(new AlbumCreateRequest(null, null, request));
    }

    @GetMapping("/search")
    AlbumsSearchResponse getAlbumByTitle(@RequestParam("title") String title) {
        return null;
    }
}
