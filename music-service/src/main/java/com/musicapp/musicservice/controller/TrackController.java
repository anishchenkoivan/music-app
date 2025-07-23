package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.TrackDto;
import com.musicapp.musicservice.dto.request.TrackDataModifyRequest;
import com.musicapp.musicservice.dto.response.TrackDataUploadResponse;
import com.musicapp.musicservice.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tracks")
public class TrackController {
    private final TrackService trackService;

    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/{id}")
    public TrackDto getTrackById(@PathVariable("id") UUID id) {
        return trackService.getTrackViewById(id);
    }

    @PostMapping("/upload")
    public TrackDataUploadResponse uploadTrackData(TrackDataModifyRequest request) {
        return trackService.createTrackData(request);
    }
}
