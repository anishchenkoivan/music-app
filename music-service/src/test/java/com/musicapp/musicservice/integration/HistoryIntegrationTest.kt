package com.musicapp.musicservice.integration

import com.musicapp.musicservice.dto.response.statistics.SimplifiedHistoryEntryResponse
import com.musicapp.musicservice.dto.response.statistics.SimplifiedUserHistoryResponse
import com.musicapp.musicservice.gateway.StatisticsClient
import com.musicapp.musicservice.gateway.StatisticsKafkaProducer
import com.musicapp.musicservice.gateway.event.HistoryEntryAddEvent
import com.musicapp.musicservice.integration.util.Spawner
import com.musicapp.musicservice.service.AlbumService
import com.musicapp.musicservice.service.ArtistService
import com.musicapp.musicservice.service.StatisticsService
import com.musicapp.musicservice.service.TrackService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest
@ExtendWith(MockKExtension::class)
class HistoryIntegrationTest : BaseIntegrationTest() {
    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun spawner(): Spawner = Spawner()
    }

    @Autowired
    private lateinit var albumService: AlbumService

    @Autowired
    private lateinit var artistService: ArtistService

    @Autowired
    private lateinit var trackService: TrackService

    @Autowired
    private lateinit var statisticsService: StatisticsService

    @MockkBean(relaxed = true)
    private lateinit var statisticsKafkaProducer: StatisticsKafkaProducer

    @MockkBean(relaxed = true)
    private lateinit var statisticsClient: StatisticsClient

    @Autowired
    private lateinit var spawner: Spawner

    @Test
    fun shouldAddHistoryEntry() {
        val userId = UUID.randomUUID()
        val trackId = UUID.randomUUID()
        statisticsService.addToHistory(userId, trackId)

        val expectedEvent = HistoryEntryAddEvent(userId, trackId)
        verify {
            statisticsKafkaProducer.addHistoryEntry(eq(expectedEvent))
        }
    }

    @Test
    fun shouldCorrectlyRepresentHistory() {
        val tracksAmount = 10
        val tracks = spawner.spawnTracks(tracksAmount)
        val userId = UUID.randomUUID()

        every {statisticsClient.getUserHistory(userId, tracksAmount)} returns SimplifiedUserHistoryResponse(
            tracks.map { SimplifiedHistoryEntryResponse(it, Instant.now()) }
        )

        val userHistory = statisticsService.getUserHistory(userId, tracksAmount).history
        assertEquals(tracksAmount, userHistory.size)
        userHistory.forEachIndexed { index, response ->
            val trackDto = response.track
            assertEquals(trackDto.id, tracks[index])
        }
    }
}