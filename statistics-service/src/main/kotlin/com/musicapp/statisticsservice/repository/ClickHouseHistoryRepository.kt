package com.musicapp.statisticsservice.repository

import com.musicapp.statisticsservice.entity.HistoryEntry
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

@Repository
class ClickHouseHistoryRepository(val jdbcTemplate: JdbcTemplate): HistoryRepository {
    private val rowMapper = { rs: ResultSet, _: Int ->
        HistoryEntry(
            userId = UUID.fromString(rs.getString("user_id")),
            trackViewId = UUID.fromString(rs.getString("track_id")),
            timestamp = rs.getTimestamp("timestamp").toInstant()
        )
    }

    override fun addEntry(historyEntry: HistoryEntry) {
        val sql = "INSERT INTO history (track_id, user_id, timestamp) VALUES (?, ?, ?)"
        jdbcTemplate.update(sql, historyEntry.trackViewId, historyEntry.userId, historyEntry.timestamp)
    }

    override fun getUserHistory(userId: UUID, limit: Int): List<HistoryEntry> {
        val sql = "SELECT * FROM history WHERE user_id = ? ORDER BY timestamp LIMIT ?"

        val result = jdbcTemplate.query(sql, rowMapper, userId, limit)
        return result
    }
}