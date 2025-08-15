package com.musicapp.musicservice.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
abstract class ElasticSearchBaseIntegrationTest: BaseIntegrationTest() {
    companion object {
        @Container
        @ServiceConnection
        val elasticSearchContainer = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.16.2").apply {
            addEnv("discovery.type", "single-node")
            addEnv("xpack.security.enabled", "false")
        }

    }
}