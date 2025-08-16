package com.musicapp.musicservice.service.events

import java.util.UUID

data class AlbumCreatedEvent(
    val id: UUID,
    val title: String,
)
