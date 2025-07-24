package com.musicapp.streamingservice.controller;

import com.musicapp.streamingservice.dto.AudioStreamingDto;
import com.musicapp.streamingservice.security.StreamingTokenService;
import com.musicapp.streamingservice.service.StreamingService;
import com.musicapp.streamingservice.util.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/audio")
public class StreamingController {
    private final StreamingService streamingService;
    private final StreamingTokenService streamingTokenService;

    @Autowired
    public StreamingController(StreamingService streamingService, StreamingTokenService streamingTokenService) {
        this.streamingService = streamingService;
        this.streamingTokenService = streamingTokenService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StreamingResponseBody> streamAudio(@PathVariable String id, @RequestParam("token") String token, @RequestHeader HttpHeaders headers) {
        if (!streamingTokenService.validateToken(token, id)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        AudioStreamingDto dto = streamingService.stream(id, headers.getFirst(HttpHeaders.RANGE));
        Range range = dto.range();
        StreamingResponseBody body = dto.body();

        HttpHeaders resp = new HttpHeaders();
        resp.set(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        resp.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        resp.setContentLength(range.length());
        if (range.isPartial()) {
            resp.set(HttpHeaders.CONTENT_RANGE,
                    "bytes " + range.start() + "-" + range.end() + "/" + range.fileSize());
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(resp)
                    .body(body);
        }
        return ResponseEntity.ok().headers(resp).body(body);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestParam("file") MultipartFile file) {
        streamingService.save(file);
    }
}
