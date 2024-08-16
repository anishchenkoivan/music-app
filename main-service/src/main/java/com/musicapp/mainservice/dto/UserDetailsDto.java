package com.musicapp.mainservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserDetailsDto(@NotEmpty String firstName, @NotEmpty String lastName, @NotEmpty @Email String username, @NotNull String bio, @NotEmpty String country, @NotEmpty String email, @NotNull String profilePicture) {
}
