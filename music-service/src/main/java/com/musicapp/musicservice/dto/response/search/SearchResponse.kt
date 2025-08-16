package com.musicapp.musicservice.dto.response.search

data class SearchResponse(
    val tracks: TrackSearchResponse,
    val albums: AlbumSearchResponse,
    val artists: ArtistSearchResponse
)
