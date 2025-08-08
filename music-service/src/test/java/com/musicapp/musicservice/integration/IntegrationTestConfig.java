package com.musicapp.musicservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapp.musicservice.integration.stabs.KafkaTestProducer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@TestConfiguration
public class IntegrationTestConfig {
    @Bean
    public KafkaTestProducer kafkaTestProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        return new KafkaTestProducer(kafkaTemplate, objectMapper);
    }
}
