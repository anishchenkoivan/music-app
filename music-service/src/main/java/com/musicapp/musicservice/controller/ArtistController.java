package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.service.ArtistService;
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

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    ArtistDto getArtistById(@PathVariable("id") UUID id) {
        return artistService.getArtistById(id);
    }
}
