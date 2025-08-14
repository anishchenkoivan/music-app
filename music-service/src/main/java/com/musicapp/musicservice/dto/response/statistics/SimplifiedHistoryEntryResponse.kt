package com.musicapp.musicservice.dto.response.statistics

import java.time.Instant
import java.util.UUID

data class SimplifiedHistoryEntryResponse(val trackId: UUID, val timestamp: Instant)
