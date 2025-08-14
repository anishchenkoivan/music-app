package com.musicapp.statisticsservice.entity

import java.time.Instant
import java.util.UUID

data class HistoryEntry(val userId: UUID, val trackViewId: UUID, val timestamp: Instant)