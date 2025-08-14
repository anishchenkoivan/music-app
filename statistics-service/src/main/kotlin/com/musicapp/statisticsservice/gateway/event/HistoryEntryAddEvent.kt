package com.musicapp.statisticsservice.gateway.event

import java.util.UUID

data class HistoryEntryAddEvent(val userId: UUID, val trackId: UUID)
