package com.musicapp.musicservice.dto.response.search

import com.musicapp.musicservice.dto.TrackDto

data class TrackSearchResponse(
    val tracks: List<TrackDto>
)
