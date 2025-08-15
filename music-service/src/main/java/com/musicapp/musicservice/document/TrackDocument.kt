package com.musicapp.musicservice.document

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.util.UUID

@Document(indexName = "tracks")
data class TrackDocument(
    @Id
    val id: UUID,

    @Field(type = FieldType.Text)
    val title: String
)
