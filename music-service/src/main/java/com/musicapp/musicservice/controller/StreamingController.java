package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.response.StreamingResponse;
import com.musicapp.musicservice.service.ListenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/stream")
public class StreamingController {
    private final ListenService listenService;
    @Value("${streaming.service.base.url}")
    private String streamingServiceUrl;

    public StreamingController(ListenService listenService) {
        this.listenService = listenService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> stream(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());

        StreamingResponse streamingResponse = listenService.listen(id, userId);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(streamingServiceUrl)
                .pathSegment("audio", id.toString())
                .queryParam("token", streamingResponse.hmacToken())
                .toUriString();

        System.out.println(redirectUrl);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
    }
}
