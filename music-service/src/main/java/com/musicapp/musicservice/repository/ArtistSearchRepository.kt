package com.musicapp.musicservice.repository

import com.musicapp.musicservice.document.ArtistDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.UUID

interface ArtistSearchRepository : ElasticsearchRepository<ArtistDocument, UUID> {
}