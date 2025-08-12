package com.musicapp.statisticsservice.controller

import com.musicapp.statisticsservice.dto.request.HistoryEntryRequest
import com.musicapp.statisticsservice.dto.request.TestRequest
import com.musicapp.statisticsservice.dto.response.TestResponse
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

    @PostMapping("/add")
    fun addToHistory(@RequestBody request: HistoryEntryRequest) {
        historyService.addEntry(request)
    }

    @GetMapping("/for-user/{userId}")
    fun getUserHistory(@PathVariable userId: UUID, @RequestParam limit: Int?) {
        historyService.getUserHistory(userId, limit ?: 10)
    }

    @PostMapping("/test")
    fun test(@RequestParam code: String?, @RequestBody body: TestRequest): TestResponse {
        return TestResponse(body.message, code ?: "default code")
    }
}