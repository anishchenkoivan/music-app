package com.musicapp.statisticsservice.integration

import com.musicapp.statisticsservice.gateway.event.HistoryEntryAddEvent
import com.musicapp.statisticsservice.service.HistoryService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest
class HistoryIntegrationTest: BaseIntegrationTest() {
    @Autowired
    lateinit var historyService: HistoryService

    @Test
    fun shouldAddHistoryEntryAndReturnIt() {
        val userId = UUID.randomUUID()
        val trackId = UUID.randomUUID()

        historyService.addEntry(HistoryEntryAddEvent(userId, trackId))

        val historyResponse = historyService.getUserHistory(userId, 1)
        assertEquals(1, historyResponse.history.size)
        assertEquals(trackId, historyResponse.history.first().trackId)
    }

    @Test
    fun shouldReturnHistoryInRightOrder() {
        val userId = UUID.randomUUID()
        val tracksAmount = 20
        val requestLimit = 5
        val tracks = List(tracksAmount) { UUID.randomUUID() }

        tracks.forEach { trackId ->
            historyService.addEntry(HistoryEntryAddEvent(userId, trackId))
            Thread.sleep(10)
        }

        val historyResponse = historyService.getUserHistory(userId, requestLimit)
        assertEquals(requestLimit, historyResponse.history.size)
        assertEquals(tracks.takeLast(requestLimit).reversed(), historyResponse.history.map { it.trackId })
    }
}