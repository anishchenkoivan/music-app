package com.musicapp.musicservice.service.events

import java.util.UUID

data class TrackViewCreatedEvent(
    val id: UUID,
    val title: String,
)
