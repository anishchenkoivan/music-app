package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.ArtistDto;
import com.musicapp.musicservice.dto.request.ArtistModifyRequest;
import com.musicapp.musicservice.dto.request.GetArtistByNameRequest;
import com.musicapp.musicservice.dto.response.ApiError;
import com.musicapp.musicservice.service.ArtistService;
import com.musicapp.musicservice.util.ArtistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/music-service/artist")
public class ArtistController {
    ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {}

    @GetMapping("/{id}")
    public ArtistDto getArtistById(@PathVariable UUID id) {
        return ArtistMapper.toArtistDto(artistService.getArtistById(id));
    }

    @GetMapping("/by-name")
    public List<ArtistDto> getArtistsByName(@RequestBody GetArtistByNameRequest request) {
        return artistService.getArtistsByName(request.artistName())
                .stream().map(ArtistMapper::toArtistDto).toList();
    }

    @PostMapping("/create")
    public UUID createArtist(@RequestBody ArtistModifyRequest artistData) {
        return artistService.createArtist(artistData);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> NoSuchElementExceptionHandler(NoSuchElementException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND);
    }
}
