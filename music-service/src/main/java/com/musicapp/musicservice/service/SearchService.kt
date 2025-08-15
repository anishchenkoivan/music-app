package com.musicapp.musicservice.service

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import com.musicapp.musicservice.document.AlbumDocument
import com.musicapp.musicservice.document.ArtistDocument
import com.musicapp.musicservice.document.TrackDocument
import com.musicapp.musicservice.dto.response.search.TrackSearchResponse
import com.musicapp.musicservice.repository.AlbumSearchRepository
import com.musicapp.musicservice.repository.ArtistSearchRepository
import com.musicapp.musicservice.repository.TrackSearchRepository
import com.musicapp.musicservice.service.events.AlbumCreatedEvent
import com.musicapp.musicservice.service.events.ArtistCreatedEvent
import com.musicapp.musicservice.service.events.TrackViewCreatedEvent
import com.musicapp.musicservice.util.QueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

@Service
class SearchService(
    private val operations: ElasticsearchOperations,

    private val trackSearchRepository: TrackSearchRepository,
    private val albumSearchRepository: AlbumSearchRepository,
    private val artistSearchRepository: ArtistSearchRepository,

    private val trackService: TrackService,
    private val albumService: AlbumService,
    private val artistService: ArtistService,

    private val queryBuilder: QueryBuilder
) {
    @TransactionalEventListener
    fun saveTrackToIndex(event: TrackViewCreatedEvent) {
        val trackDocument = TrackDocument(event.id, event.title)
        trackSearchRepository.save(trackDocument)
    }

    fun saveAlbumToIndex(event: AlbumCreatedEvent) {
        val albumDocument = AlbumDocument(event.id, event.title)
        albumSearchRepository.save(albumDocument)
    }

    fun saveArtistToIndex(event: ArtistCreatedEvent) {
        val artistDocument = ArtistDocument(event.id, event.name)
        artistSearchRepository.save(artistDocument)
    }

    fun searchTrack(title: String) : TrackSearchResponse {
        val query = queryBuilder.singleFieldQuery("title", title)
        val result = operations.search(query, TrackDocument::class.java).map { it.content }
        val foundTracks = trackService.getTrackViewsById(result.map { it.id }.toMutableList())
        return TrackSearchResponse(foundTracks)
    }

    fun searchAlbum() {

    }

    fun searchArtist() {

    }

    fun searchAll() {

    }
}