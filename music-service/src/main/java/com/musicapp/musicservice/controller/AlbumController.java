package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.AlbumDto;
import com.musicapp.musicservice.dto.request.AlbumCreateRequest;
import com.musicapp.musicservice.dto.request.AlbumGeneralCreateRequest;
import com.musicapp.musicservice.dto.response.AlbumCreateResponse;
import com.musicapp.musicservice.dto.response.AlbumsSearchResponse;
import com.musicapp.musicservice.service.AlbumService;
import com.musicapp.musicservice.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController()
@RequestMapping("/albums")
public class AlbumController {
    private final AlbumService albumService;
    private final ArtistService artistService;

    @Autowired
    public AlbumController(AlbumService albumService, ArtistService artistService) {
        this.albumService = albumService;
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    AlbumDto getAlbumById(@PathVariable("id") UUID id) {
        return albumService.getAlbumById(id);
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    AlbumCreateResponse createAlbum(@RequestBody AlbumGeneralCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return albumService.createAlbum(new AlbumCreateRequest(
                artistService.getArtistForUser(UUID.fromString(authentication.getPrincipal().toString())).id(),
                LocalDate.now(),
                request
        ));
    }

    @GetMapping("/search")
    AlbumsSearchResponse getAlbumByTitle(@RequestParam("title") String title) {
        return null;
    }
}
