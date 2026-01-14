# Streaming Service Documentation

## Overview

The Streaming Service handles audio file storage and streaming for the music application. It supports efficient byte-range streaming for audio playback and integrates with Kafka to publish track upload events.

## Technology Stack

- **Language**: Java
- **Framework**: Spring Boot
- **Storage**: MinIO (S3-compatible object storage)
- **Message Queue**: Apache Kafka
- **Build Tool**: Gradle (Kotlin DSL)

## Service Configuration

- **Port**: 8085
- **Service Name**: `streaming-service`
- **Storage**: MinIO (ports 9001-9002)
- **Eureka Registration**: Enabled

## Key Components

### Controllers

#### StreamingController
**Path**: `/stream`

**Endpoints**:
- `GET /stream/{id}` - Stream audio file with range support
- `POST /stream/upload` - Upload audio file

### Services

#### StreamingService
**Path**: [`StreamingService.java`](../../streaming-service/src/main/java/com/musicapp/streamingservice/service/StreamingService.java)

**Responsibilities**:
- Audio file upload to MinIO
- Range-based audio streaming
- MP3 format validation
- Duration extraction
- Kafka event publishing

**Key Methods**:
- `stream(String id, String range)` - Stream audio with byte-range support
- `save(MultipartFile file, String id)` - Upload and validate MP3 file

### Repository

#### StreamingRepository
**Purpose**: MinIO storage abstraction
**Responsibilities**:
- Audio file storage in MinIO
- File size retrieval
- Range-based streaming
- Object management

**Key Methods**:
- `save(MultipartFile, String)` - Save audio file
- `stream(String, long, long)` - Stream file with byte range
- `size(String)` - Get file size
- `exists(String)` - Check file existence

### Utilities

#### AudioUtil
**Purpose**: Audio file validation and metadata extraction
**Responsibilities**:
- MP3 format validation
- Duration extraction from MP3 headers
- Audio quality validation

**Key Methods**:
- `isMp3File(MultipartFile)` - Validate MP3 format
- `getMp3Duration(MultipartFile)` - Extract duration in seconds

#### Range
**Purpose**: HTTP Range header parsing
**Responsibilities**:
- Parse Range header (e.g., "bytes=0-1023")
- Calculate start and end positions
- Handle partial content requests

**Key Methods**:
- `parse(String range, long fileSize)` - Parse range header
- `getStart()` - Get start byte position
- `getEnd()` - Get end byte position
- `getLength()` - Get range length

### Kafka Integration

#### UploadKafkaProducer
**Purpose**: Publish track upload events
**Responsibilities**:
- Send TrackUploadedEvent to Kafka
- Event serialization

**Events Published**:
- `TrackUploadedEvent` - Published when audio file uploaded

## API Endpoints

### GET /stream/{id}
**Description**: Stream audio file with HTTP range support (206 Partial Content)

**Path Parameters**:
- `id` (String) - Track ID

**Headers**:
- `Range: bytes={start}-{end}` (optional)

**Response Headers**:
- `Content-Type: audio/mpeg`
- `Content-Length: {size}`
- `Accept-Ranges: bytes`
- `Content-Range: bytes {start}-{end}/{total}` (if range requested)

**Response**: Audio binary stream

**Status Codes**:
- `200 OK` - Full file stream (no range)
- `206 PARTIAL CONTENT` - Range-based stream
- `404 NOT FOUND` - Track not found
- `416 RANGE NOT SATISFIABLE` - Invalid range

**Range Request Examples**:
```
Range: bytes=0-1023        # First 1KB
Range: bytes=1024-2047     # Second 1KB
Range: bytes=0-            # From start to end
Range: bytes=-1024         # Last 1KB
```

**Use Cases**:
- Progressive audio playback
- Seeking in audio player
- Bandwidth optimization
- Resume interrupted downloads

---

### POST /stream/upload
**Description**: Upload MP3 audio file with JWT upload token

**Headers**:
- `Authorization: Bearer <upload-token>`
- `Content-Type: multipart/form-data`

**Request Body**:
- `file` (MultipartFile) - MP3 audio file
- `id` (String) - Track ID from token

**Supported Format**:
- MP3 (.mp3) only

**Size Limits**:
- Maximum: 100 MB (configurable)
- Minimum: 100 KB

**Response**: `200 OK` (no body)

**Status Codes**:
- `200 OK` - File uploaded successfully
- `400 BAD REQUEST` - Invalid file format or not MP3
- `401 UNAUTHORIZED` - Invalid or expired token
- `413 PAYLOAD TOO LARGE` - File exceeds size limit

**Process**:
1. Validate JWT upload token
2. Extract track ID from token
3. Validate MP3 format
4. Extract audio duration
5. Upload to MinIO bucket
6. Publish TrackUploadedEvent to Kafka
7. Music Service updates track as valid

## MinIO Integration

### Bucket Structure
```
minio/
└── audio/
    ├── {track-id}.mp3
    ├── {track-id}.mp3
    └── ...
```

### Storage Configuration
- **Endpoint**: `http://minio:9002` (internal)
- **Bucket**: `audio`
- **Access**: Via access key and secret key
- **Auto-creation**: Bucket created on first upload

### Object Naming Convention
- **Format**: `{trackId}.mp3`
- **Versioning**: Not enabled (overwrites on re-upload)
- **Metadata**: Content-Type, duration stored

## HTTP Range Streaming

### How It Works
1. Client requests audio with Range header
2. Service parses range (start-end bytes)
3. MinIO streams only requested byte range
4. Response includes Content-Range header
5. Client can request multiple ranges for seeking

### Benefits
- **Bandwidth Efficiency**: Only stream needed bytes
- **Fast Seeking**: Jump to any position instantly
- **Resume Support**: Continue interrupted streams
- **Mobile Friendly**: Reduce data usage

### Implementation
```java
// Parse range header
Range range = Range.parse(requestedRange, fileSize);

// Stream from MinIO
InputStream stream = repository.stream(fileName, range.start(), range.end());

// Return with proper headers
return ResponseEntity.status(206)
    .header("Content-Range", "bytes " + range.start() + "-" + range.end() + "/" + fileSize)
    .header("Accept-Ranges", "bytes")
    .body(stream);
```

## Kafka Integration

### Events Published

#### TrackUploadedEvent
**Topic**: `track-uploaded`

**Payload**:
```json
{
  "trackId": "uuid",
  "duration": 180
}
```

**Purpose**: Notify Music Service that audio file is uploaded and valid

**Consumers**:
- Music Service: Updates track `is_valid` flag and duration

### Event Flow
```
1. Client uploads MP3 → Streaming Service
2. Streaming Service validates and stores file
3. Streaming Service extracts duration
4. Streaming Service publishes TrackUploadedEvent
5. Music Service consumes event
6. Music Service marks track as valid
7. Track becomes available for streaming
```

## Audio Validation

### MP3 Format Validation
- Check file extension (.mp3)
- Verify MIME type (audio/mpeg)
- Validate MP3 headers (magic bytes)
- Check for valid MP3 frames

### Quality Validation (Future)
- Bitrate validation (minimum 128 kbps)
- Sample rate validation (44.1 kHz, 48 kHz)
- Stereo/mono validation
- Duration validation (minimum 10 seconds)

### Metadata Extraction
- Duration in seconds
- Bitrate
- Sample rate
- ID3 tags (title, artist, album)

## Security

### Upload Token Validation
- Token must be present in Authorization header
- Token format: `Bearer {jwt-token}`
- Token must be valid and not expired
- Token must contain track ID
- Track ID must match upload request

### Streaming Security
- Public streaming (no authentication currently)
- Consider signed URLs for premium content
- Rate limiting to prevent abuse
- DRM support (future)

## Configuration Properties

```properties
server.port=8085
spring.application.name=streaming-service

# MinIO
minio.endpoint=${MINIO_ENDPOINT:http://localhost:9002}
minio.access-key=${MINIO_ACCESS_KEY:minioadmin}
minio.secret-key=${MINIO_SECRET_KEY:minioadmin}
minio.bucket.audio=audio

# Kafka
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# File Upload
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka}
```

## Performance Considerations

### Streaming Optimization
- Direct streaming from MinIO (no buffering)
- Efficient byte-range handling
- Connection pooling to MinIO
- Async I/O for concurrent streams

### Upload Optimization
- Stream directly to MinIO (no temp files)
- Async upload processing
- Parallel uploads (future)
- Chunked upload for large files (future)

### Storage Optimization
- Audio compression (future)
- Format conversion (future)
- Bitrate optimization
- Unused file cleanup

### Caching Strategy
- CDN for popular tracks
- Edge caching for reduced latency
- Cache-Control headers
- ETags for conditional requests

## Testing

### Unit Tests
**Location**: [`src/test/java/com/musicapp/streamingservice/unit/`](../../streaming-service/src/test/java/com/musicapp/streamingservice/unit/)

**Test Coverage**:
- Range parsing logic
- MP3 validation
- Duration extraction
- Streaming logic

### Integration Tests
- MinIO container testing
- End-to-end upload/stream
- Range request handling
- Kafka event publishing

## Dependencies

### Key Libraries
- Spring Boot Starter Web
- MinIO Java SDK
- Apache Kafka Clients
- Spring Kafka
- MP3 Parser Library (for metadata)
- Spring Cloud Netflix Eureka Client

## Deployment

### Docker
**Dockerfile**: [`streaming-service/Dockerfile`](../../streaming-service/Dockerfile)

**Environment Variables**:
- `MINIO_ENDPOINT` - MinIO server URL
- `MINIO_ACCESS_KEY` - MinIO access key
- `MINIO_SECRET_KEY` - MinIO secret key
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka brokers
- `EUREKA_URL` - Eureka server URL

### Dependencies
- MinIO container running
- Kafka cluster available
- Music Service (event consumer)
- Network connectivity

## Monitoring & Health

### Health Checks
- MinIO connectivity
- Kafka producer health
- Bucket accessibility
- Disk space monitoring

### Metrics
- Upload success/failure rate
- Average upload time
- Stream request count
- Bandwidth usage
- Concurrent streams
- Average stream duration

### Logging
- Upload attempts and results
- Stream requests
- Range request details
- MinIO connection errors
- Kafka publishing failures

## Error Scenarios

### Upload Failures
1. **Invalid Format**: Returns 400, not MP3
2. **MinIO Unavailable**: Returns 500, retry
3. **File Too Large**: Returns 413
4. **Invalid Token**: Returns 401
5. **Kafka Unavailable**: Upload succeeds, event queued

### Streaming Failures
1. **File Not Found**: Returns 404
2. **MinIO Unavailable**: Returns 500
3. **Invalid Range**: Returns 416
4. **Corrupted File**: Returns 500

## Integration with Other Services

### Music Service
**Flow**:
1. User creates track in Music Service
2. Music Service generates upload token
3. Client uploads MP3 to Streaming Service
4. Streaming Service publishes TrackUploadedEvent
5. Music Service marks track as valid

### Gateway
**Routing**: `/api/stream/**` → Streaming Service

### CDN (Future)
- Audio served through CDN
- MinIO as origin server
- Cache popular tracks
- Reduce origin load

## Audio Formats Support (Future)

### Current Support
- MP3 only

### Planned Support
- [ ] AAC (.m4a)
- [ ] FLAC (lossless)
- [ ] OGG Vorbis
- [ ] Opus
- [ ] WAV (upload, convert to MP3)

### Transcoding Pipeline
- Accept multiple formats on upload
- Transcode to MP3 for streaming
- Store original for quality
- Generate multiple bitrates (adaptive streaming)

## Future Enhancements

- [ ] Adaptive bitrate streaming (HLS/DASH)
- [ ] Multiple audio quality options
- [ ] Audio transcoding pipeline
- [ ] Waveform generation
- [ ] Audio fingerprinting
- [ ] DRM support
- [ ] Offline download support
- [ ] Lyrics synchronization
- [ ] Audio normalization
- [ ] Gapless playback support
- [ ] Crossfade support
- [ ] Equalizer presets
- [ ] Audio analytics (listening patterns)
- [ ] CDN integration
- [ ] Multi-region storage
- [ ] Automatic backup
- [ ] Storage tiering (hot/cold)
