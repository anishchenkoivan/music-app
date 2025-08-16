package com.musicapp.musicservice.repository

import com.musicapp.musicservice.document.AlbumDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.UUID

interface AlbumSearchRepository : ElasticsearchRepository<AlbumDocument, UUID> {
}