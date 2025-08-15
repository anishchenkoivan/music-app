package com.musicapp.musicservice.repository

import com.musicapp.musicservice.document.TrackDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.UUID

interface TrackSearchRepository : ElasticsearchRepository<TrackDocument, UUID> {
}