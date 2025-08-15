package com.musicapp.musicservice.service.events

import java.util.UUID

data class ArtistCreatedEvent(
    val id: UUID,
    val name: String,
)
