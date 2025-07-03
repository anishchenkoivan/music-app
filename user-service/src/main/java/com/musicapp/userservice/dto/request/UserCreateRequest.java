package com.musicapp.userservice.dto.request;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotEmpty;


public record UserCreateRequest(UserModifyRequest userModifyRequest, @NotEmpty String password) {
    @JsonUnwrapped
    public UserModifyRequest userModifyRequest() {
        return userModifyRequest;
    }
}
