package com.musicapp.musicservice.gateway;

import com.musicapp.musicservice.dto.response.statistics.SimplifiedUserHistoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "statistics-service"
)
public interface StatisticsClient {
    @GetMapping("/history/for-user/{userId}")
    SimplifiedUserHistoryResponse getUserHistory(@PathVariable("userId") UUID userId, @RequestParam Integer limit);
}
