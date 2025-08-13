package com.musicapp.statisticsservice.controller

import com.musicapp.statisticsservice.dto.request.HistoryEntryAddRequest
import com.musicapp.statisticsservice.dto.response.UserHistoryResponse
import com.musicapp.statisticsservice.service.HistoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/history")
class HistoryController(val historyService: HistoryService) {

    @GetMapping("/for-user/{userId}")
    fun getUserHistory(@PathVariable userId: UUID, @RequestParam limit: Int?) : UserHistoryResponse {
        return historyService.getUserHistory(userId, limit ?: 10)
    }
}