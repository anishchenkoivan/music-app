package com.musicapp.statisticsservice.gateway

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.musicapp.statisticsservice.exception.KafkaConsumeException
import com.musicapp.statisticsservice.gateway.event.HistoryEntryAddEvent
import com.musicapp.statisticsservice.service.HistoryService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class StatisticsKafkaConsumer(
    private val objectMapper: ObjectMapper,
    private val historyService: HistoryService,
) {
    @KafkaListener(topics = [$$"${history-topic}"], groupId = $$"${history-consumer-group}")
    fun onHistoryAdded(event: String) {
        try {
            val event = objectMapper.readValue(event, HistoryEntryAddEvent::class.java)
            historyService.addEntry(event)
        } catch (e: JsonProcessingException) {
            throw KafkaConsumeException(e.message, e)
        }
    }
}