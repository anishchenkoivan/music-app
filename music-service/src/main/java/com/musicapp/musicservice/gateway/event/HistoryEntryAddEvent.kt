package com.musicapp.musicservice.gateway.event

import java.util.UUID

data class HistoryEntryAddEvent(val userId: UUID, val trackId: UUID)
