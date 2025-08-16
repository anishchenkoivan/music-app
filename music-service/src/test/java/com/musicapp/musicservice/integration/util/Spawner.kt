package com.musicapp.musicservice.integration.util

import com.musicapp.musicservice.dto.request.AlbumCreateRequest
import com.musicapp.musicservice.dto.request.AlbumGeneralCreateRequest
import com.musicapp.musicservice.dto.request.ArtistModifyRequest
import com.musicapp.musicservice.dto.request.TrackDataModifyRequest
import com.musicapp.musicservice.dto.request.TrackViewCreateRequest
import com.musicapp.musicservice.gateway.event.TrackDataUploadedEvent
import com.musicapp.musicservice.service.AlbumService
import com.musicapp.musicservice.service.ArtistService
import com.musicapp.musicservice.service.TrackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import java.time.LocalDate
import java.util.UUID

@TestComponent
class Spawner {
    @Autowired
    private lateinit var albumService: AlbumService

    @Autowired
    private lateinit var artistService: ArtistService

    @Autowired
    private lateinit var trackService: TrackService

    fun spawnTracks(titles: List<String>) : List<UUID> {
        val artistId = artistService.createArtist(ArtistModifyRequest("Artist")).id
        val trackDataId = trackService.createTrackData(TrackDataModifyRequest("Title", setOf(artistId))).id
        trackService.trackDataUploaded(TrackDataUploadedEvent(trackDataId, 2))
        val tracksRequest = List(titles.size) {TrackViewCreateRequest(titles[it], trackDataId)}
        val albumDto = albumService.createAlbum(AlbumCreateRequest(artistId, LocalDate.now(), AlbumGeneralCreateRequest("Title", tracksRequest)))
        return albumDto.tracks.map { it.id }
    }

    fun spawnTracks(size: Int) = spawnTracks(List(size) { "Title-$it"})

    fun spawnAlbums(titles: List<String>) : List<UUID> {
        val artistId = artistService.createArtist(ArtistModifyRequest("Artist")).id
        val trackDataId = trackService.createTrackData(TrackDataModifyRequest("Title", setOf(artistId))).id
        trackService.trackDataUploaded(TrackDataUploadedEvent(trackDataId, 2))
        val tracksRequest = listOf(TrackViewCreateRequest("-", trackDataId))
        return titles.map { albumService.createAlbum(AlbumCreateRequest(artistId, LocalDate.now(), AlbumGeneralCreateRequest(it, tracksRequest))).id }
    }

    fun spawnArtists(names: List<String>) : List<UUID> {
        return names.map { artistService.createArtist(ArtistModifyRequest(it)).id }
    }
}