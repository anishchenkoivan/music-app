package com.musicapp.musicservice.dto.request;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.time.LocalDate;
import java.util.UUID;

public record AlbumCreateRequest(
        UUID artistId,
        LocalDate releaseDate,
        @JsonUnwrapped
        AlbumGeneralCreateRequest generalData
) {
}
