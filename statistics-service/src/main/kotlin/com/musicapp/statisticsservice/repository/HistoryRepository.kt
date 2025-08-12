package com.musicapp.statisticsservice.repository

import com.musicapp.statisticsservice.entity.HistoryEntry
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface HistoryRepository {
    fun addEntry(historyEntry: HistoryEntry)
    fun getUserHistory(userId: UUID, limit: Int): List<HistoryEntry>
}