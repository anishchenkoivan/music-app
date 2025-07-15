package com.musicapp.streamingservice.dto;

import com.musicapp.streamingservice.util.Range;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record AudioStreamingDto(Range range, StreamingResponseBody body) {
}
