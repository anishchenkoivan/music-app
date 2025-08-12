package com.musicapp.statisticsservice.dto.request

import java.util.UUID

data class HistoryEntryRequest(val userId: UUID, val trackViewId: UUID)
