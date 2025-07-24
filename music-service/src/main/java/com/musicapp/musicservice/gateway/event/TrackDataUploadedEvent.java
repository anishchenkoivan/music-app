package com.musicapp.musicservice.gateway.event;

import java.util.UUID;

public record TrackDataUploadedEvent(UUID trackDataId) {
}
