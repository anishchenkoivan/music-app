package com.musicapp.statisticsservice.dto.request

import java.util.UUID

data class HistoryEntryAddRequest(val userId: UUID, val trackViewId: UUID)
