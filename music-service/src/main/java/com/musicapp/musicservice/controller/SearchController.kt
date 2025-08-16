package com.musicapp.musicservice.controller

import com.musicapp.musicservice.dto.response.search.AlbumSearchResponse
import com.musicapp.musicservice.dto.response.search.ArtistSearchResponse
import com.musicapp.musicservice.dto.response.search.SearchResponse
import com.musicapp.musicservice.dto.response.search.TrackSearchResponse
import com.musicapp.musicservice.service.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(private val searchService: SearchService) {
    @GetMapping("/tracks/search")
    fun searchTracks(@RequestParam q: String) : TrackSearchResponse {
        return searchService.searchTrack(q)
    }

    @GetMapping("/albums/search")
    fun searchAlbums(@RequestParam q: String) : AlbumSearchResponse {
        return searchService.searchAlbum(q)
    }

    @GetMapping("/artists/search")
    fun searchArtists(@RequestParam q: String) : ArtistSearchResponse {
        return searchService.searchArtist(q)
    }

    @GetMapping("/search")
    fun search(@RequestParam q: String) : SearchResponse {
        return searchService.searchAll(q)
    }
}