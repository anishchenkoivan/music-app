package com.musicapp.statisticsservice.service

import com.musicapp.statisticsservice.dto.response.SimplifiedHistoryEntryResponse
import com.musicapp.statisticsservice.dto.response.UserHistoryResponse
import com.musicapp.statisticsservice.entity.HistoryEntry
import com.musicapp.statisticsservice.gateway.event.HistoryEntryAddEvent
import com.musicapp.statisticsservice.repository.HistoryRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class HistoryService(private val historyRepository: HistoryRepository) {
    fun addEntry(event: HistoryEntryAddEvent) {
        val historyEntry = with (event) {
            HistoryEntry(userId, trackId, Instant.now())
        }
        historyRepository.addEntry(historyEntry)
    }

    fun getUserHistory(userId: UUID, limit: Int): UserHistoryResponse {
        val history = historyRepository.getUserHistory(userId, limit)
        return UserHistoryResponse(history.map { SimplifiedHistoryEntryResponse(it.trackViewId, it.timestamp) })
    }
}