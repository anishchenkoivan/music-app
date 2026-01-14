package com.musicapp.musicservice.controller;

import com.musicapp.musicservice.dto.response.streaming.StreamingResponse;
import com.musicapp.musicservice.service.ActionService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/stream")
public class StreamingController {
    private final ActionService actionService;
    @Value("${streaming.service.base.url}")
    private String streamingServiceUrl;

    public StreamingController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> stream(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());

        StreamingResponse streamingResponse = actionService.listen(id, userId);

        // Return relative URL so it goes through the gateway/proxy
        String streamUrl = "/audio/" + id.toString() + "?token=" + streamingResponse.hmacToken();
        
        Map<String, String> response = new HashMap<>();
        response.put("url", streamUrl);

        return ResponseEntity.ok(response);
    }
}
