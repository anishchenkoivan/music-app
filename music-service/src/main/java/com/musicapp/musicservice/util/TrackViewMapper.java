package com.musicapp.musicservice.util;

import com.musicapp.musicservice.dto.TrackViewDto;
import com.musicapp.musicservice.entity.TrackView;

public class TrackViewMapper {
    public static TrackView toTrackView(TrackViewDto trackViewDto) {
        return new TrackView();
    }

    public static TrackViewDto toTrackViewDto(TrackView trackView) {
        return new TrackViewDto();
    }
}
