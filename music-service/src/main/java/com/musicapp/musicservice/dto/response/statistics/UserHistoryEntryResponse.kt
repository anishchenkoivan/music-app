package com.musicapp.musicservice.dto.response.statistics

import com.musicapp.musicservice.dto.TrackDto
import java.time.Instant

data class UserHistoryEntryResponse(val track: TrackDto, val timestamp: Instant)
