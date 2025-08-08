package com.musicapp.streamingservice.gateway.event;

import java.util.UUID;

public record TrackUploadedEvent(UUID id, int duration) {
}
