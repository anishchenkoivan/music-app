package com.musicapp.musicservice.integration

import com.musicapp.musicservice.dto.response.search.TrackSearchResponse
import com.musicapp.musicservice.integration.util.Spawner
import com.musicapp.musicservice.service.SearchService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import kotlin.test.assertContains
import kotlin.test.assertTrue

@SpringBootTest
class SearchTest : ElasticSearchBaseIntegrationTest() {
    @TestConfiguration
    open class TestConfig {
        @Bean
        open fun spawner(): Spawner = Spawner()
    }

    @Autowired
    private lateinit var searchService: SearchService

    @Autowired
    private lateinit var spawner: Spawner

    @Test
    fun shouldFindTrack() {
        val titles = listOf("Title", "TitleA", "A title", "Two words", "Two, words!", "Three words title")
        spawner.spawnTracks(titles)

        var result: TrackSearchResponse? = null;
        result = searchService.searchTrack("Title")
        assertContains(result.tracks.map { it.title }, "Title")

        result = searchService.searchTrack("TitleA")
        assertTrue(result.tracks.map { it.title }.containsAll(listOf("TitleA", "A title")))

        result = searchService.searchTrack("Two words")
        assertTrue(result.tracks.map { it.title }.containsAll(listOf("Two words", "Two, words!")))

        result = searchService.searchTrack("Three words title")
        assertContains(result.tracks.map { it.title }, "Three words title")

        result = searchService.searchTrack("words two")
        assertContains(result.tracks.map { it.title }, "Two words")
    }
}