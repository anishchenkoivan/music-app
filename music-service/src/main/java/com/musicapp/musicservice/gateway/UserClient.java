package com.musicapp.musicservice.gateway;

import com.musicapp.musicservice.dto.response.user.PublicUserDetailsDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        path = "/user"
)
public interface UserClient {
    @GetMapping("/{id}")
    PublicUserDetailsDtoResponse getUserDetails(@PathVariable("id") String userId);
}
