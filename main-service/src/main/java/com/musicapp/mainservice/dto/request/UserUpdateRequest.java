package com.musicapp.mainservice.dto.request;

import com.musicapp.mainservice.dto.UserDetailsDto;

public record UserUpdateRequest(UserDetailsDto userDetails, String password) {
}
