package com.musicapp.statisticsservice.integration

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.clickhouse.ClickHouseContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
abstract class BaseIntegrationTest() {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    companion object {
        @Container
        @ServiceConnection
        val clickhouse = ClickHouseContainer("clickhouse/clickhouse-server")
    }

    @BeforeEach
    fun clearTable() {
        jdbcTemplate.execute("TRUNCATE TABLE history")
    }
}