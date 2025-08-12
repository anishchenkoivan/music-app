package com.musicapp.statisticsservice.dto.response

import java.time.Instant
import java.util.UUID

data class SimplifiedHistoryEntryResponse(val trackId: UUID, val timestamp: Instant)
