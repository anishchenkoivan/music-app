package com.musicapp.musicservice.service

import com.musicapp.musicservice.dto.response.statistics.UserHistoryEntryResponse
import com.musicapp.musicservice.dto.response.statistics.UserHistoryResponse
import com.musicapp.musicservice.gateway.StatisticsClient
import com.musicapp.musicservice.gateway.StatisticsKafkaProducer
import com.musicapp.musicservice.gateway.event.HistoryEntryAddEvent
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class StatisticsService(
    private val statisticsClient: StatisticsClient,
    private val trackService: TrackService,
    private val statisticsKafkaProducer: StatisticsKafkaProducer
    ) {
    fun getUserHistory(userId: UUID, limit: Int?) : UserHistoryResponse {
        val simplifiedHistory = statisticsClient.getUserHistory(userId, limit)
        val tracks = trackService.getTrackViewsById(simplifiedHistory.history.map { it.trackId })
        return UserHistoryResponse(
            history = tracks.zip(simplifiedHistory.history) { track, simplifiedHistoryEntry ->
                UserHistoryEntryResponse(
                    track = track,
                    timestamp = simplifiedHistoryEntry.timestamp
                )
            }
        )
    }

    fun addToHistory(userId: UUID, trackViewId: UUID) {
        statisticsKafkaProducer.addHistoryEntry(HistoryEntryAddEvent(userId, trackViewId))
    }
}
