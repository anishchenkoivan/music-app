package com.musicapp.musicservice.gateway

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.musicapp.musicservice.exception.KafkaProduceException
import com.musicapp.musicservice.gateway.event.HistoryEntryAddEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class StatisticsKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @param:Value($$"${history-topic}")
    private val historyTopic: String,
    private val objectMapper: ObjectMapper
) {

    fun addHistoryEntry(event: HistoryEntryAddEvent) {
        try {
            val json = objectMapper.writeValueAsString(event)
            kafkaTemplate.send(historyTopic, json)
        } catch (e: JsonProcessingException) {
            throw KafkaProduceException(e.message, e)
        }
    }
}