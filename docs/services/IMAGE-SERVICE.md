# Image Service Documentation

## Overview

The Image Service manages image storage and retrieval for the music streaming application, primarily handling album artwork. It uses MinIO for object storage and JWT tokens for secure upload authentication.

## Technology Stack

- **Language**: Kotlin
- **Framework**: Spring Boot
- **Storage**: MinIO (S3-compatible object storage)
- **Security**: JWT validation
- **Build Tool**: Gradle (Kotlin DSL)

## Service Configuration

- **Port**: 8084
- **Service Name**: `image-service`
- **Storage**: MinIO (ports 9001-9002)
- **Eureka Registration**: Enabled

## Key Components

### Controllers

#### ImageController
**Path**: [`/images`](../../image-service/src/main/kotlin/com/musicapps/imageservice/controller/ImageController.kt)

**Endpoints**:
- `POST /images/artwork/upload` - Upload album artwork
- `GET /images/artwork/{id}` - Retrieve album artwork

### Services

#### ImageService
**Responsibilities**:
- Image upload to MinIO
- Image retrieval from MinIO
- Image validation (format, size)
- Storage management

**Key Methods**:
- `save(MultipartFile, String, ImageType)` - Save image to MinIO
- `getImage(String, ImageType)` - Retrieve image from MinIO
- `validateImage(MultipartFile)` - Validate image format and size

### Repository

#### MinioImageRepository
**Purpose**: MinIO client wrapper
**Responsibilities**:
- Direct interaction with MinIO API
- Bucket management
- Object upload/download
- Metadata handling

**Key Methods**:
- `upload(InputStream, String, String)` - Upload to MinIO
- `download(String, String)` - Download from MinIO
- `getStats(String, String)` - Get object statistics
- `ensureBucketExists(String)` - Create bucket if not exists

### Security

#### JwtService
**Purpose**: JWT token validation for uploads
**Responsibilities**:
- Validate upload tokens
- Extract resource ID from token
- Verify token signature and expiration

**Token Claims**:
- `uploadId` - Album/Track ID for upload
- `exp` - Token expiration
- `iat` - Token issued at

### Configuration

#### MinioConfig
**Purpose**: MinIO client configuration
**Beans**:
- `MinioClient` - Configured MinIO client instance

#### MinioProps
**Properties**:
- `endpoint` - MinIO server URL
- `accessKey` - MinIO access key
- `secretKey` - MinIO secret key
- `buckets` - Bucket names for different image types

### Data Model

#### ImageDto
**Fields**:
- `body` (ByteArray) - Image binary data
- `type` (MediaType) - Content type (image/jpeg, image/png, etc.)
- `size` (Long) - File size in bytes

#### UploadDto
**Fields**:
- `id` (String) - Resource ID from JWT token
- `type` (ImageType) - Type of upload

#### ImageType Enum
**Values**:
- `ARTWORK` - Album artwork
- `AVATAR` - User avatar (future)
- `BANNER` - Artist banner (future)

## API Endpoints

### POST /images/artwork/upload
**Description**: Upload album artwork with JWT upload token

**Headers**:
- `Authorization: Bearer <upload-token>`
- `Content-Type: multipart/form-data`

**Request Body**:
- `file` (MultipartFile) - Image file

**Supported Formats**:
- JPEG (.jpg, .jpeg)
- PNG (.png)
- WebP (.webp)

**Size Limits**:
- Maximum: 10 MB
- Minimum: 1 KB

**Response**: `200 OK` (no body)

**Status Codes**:
- `200 OK` - Image uploaded successfully
- `400 BAD REQUEST` - Invalid image format or size
- `401 UNAUTHORIZED` - Invalid or expired token
- `413 PAYLOAD TOO LARGE` - File exceeds size limit

**Process**:
1. Extract JWT token from Authorization header
2. Validate token and extract album ID
3. Validate image format and size
4. Upload to MinIO bucket `artwork`
5. Store with filename: `{albumId}.{extension}`

---

### GET /images/artwork/{id}
**Description**: Retrieve album artwork

**Path Parameters**:
- `id` (String) - Album ID

**Response**: Image binary data

**Headers**:
- `Content-Type: image/jpeg` (or appropriate type)
- `Content-Length: {size}`

**Status Codes**:
- `200 OK` - Image retrieved successfully
- `404 NOT FOUND` - Image not found

**Caching**:
- Consider adding Cache-Control headers
- ETags for conditional requests

## MinIO Integration

### Bucket Structure
```
minio/
├── artwork/          # Album artwork
│   ├── {album-id}.jpg
│   ├── {album-id}.png
│   └── ...
├── avatars/          # User avatars (future)
└── banners/          # Artist banners (future)
```

### Storage Configuration
- **Endpoint**: `http://minio:9002` (internal) or `http://localhost:9002` (dev)
- **Console**: `http://localhost:9001`
- **Access**: Via access key and secret key
- **Buckets**: Auto-created on first upload

### Object Naming Convention
- **Artwork**: `{albumId}.{extension}`
- **Versioning**: Not enabled (overwrites on re-upload)
- **Metadata**: Content-Type stored with object

## Security

### Upload Token Validation
1. Token must be present in Authorization header
2. Token format: `Bearer {jwt-token}`
3. Token must be valid and not expired
4. Token must contain `uploadId` claim
5. Upload ID must match resource being uploaded

### Token Generation
- Tokens generated by Music Service on album/track creation
- Short-lived (typically 1 hour)
- Single-use recommended (not enforced currently)

### Access Control
- Upload requires valid JWT token
- Download is public (no authentication)
- Consider adding signed URLs for private images

## Exception Handling

### AuthException
**Thrown when**: JWT token invalid or missing
**HTTP Status**: 401 UNAUTHORIZED
**Message**: "Authentication error" or specific token error

### ImageUploadException
**Thrown when**: Image upload to MinIO fails
**HTTP Status**: 500 INTERNAL SERVER ERROR
**Message**: Details of upload failure

### ImageGetException
**Thrown when**: Image retrieval from MinIO fails
**HTTP Status**: 404 NOT FOUND or 500 INTERNAL SERVER ERROR
**Message**: Details of retrieval failure

## Configuration Properties

```yaml
# application.yml
server:
  port: 8084

spring:
  application:
    name: image-service
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9002}
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin}
  buckets:
    artwork: artwork
    avatars: avatars
    banners: banners

jwt:
  secret: ${JWT_SECRET:your-secret-key}

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
```

## Image Validation

### Format Validation
- Check file extension
- Verify MIME type
- Validate image headers (magic bytes)

### Size Validation
- Minimum size: 1 KB
- Maximum size: 10 MB
- Configurable via properties

### Dimension Validation (Future)
- Minimum dimensions: 300x300
- Maximum dimensions: 3000x3000
- Aspect ratio validation

### Content Validation (Future)
- Malware scanning
- Content policy enforcement
- NSFW detection

## Performance Considerations

### Upload Optimization
- Stream directly to MinIO (no temp files)
- Async upload processing
- Compression before storage

### Download Optimization
- Direct streaming from MinIO
- CDN integration (future)
- Image resizing on-the-fly (future)
- Caching layer (Redis/CDN)

### Storage Optimization
- Image compression
- Format conversion (WebP)
- Thumbnail generation
- Unused image cleanup

## Testing

### Unit Tests
**Location**: [`src/test/kotlin/com/musicapps/imageservice/unit/`](../../image-service/src/test/kotlin/com/musicapps/imageservice/unit/)

**Test Coverage**:
- Image upload logic
- JWT token validation
- MinIO repository operations
- Image validation rules

### Integration Tests
- MinIO container testing
- End-to-end upload/download
- Token validation flow

## Dependencies

### Key Libraries
- Spring Boot Starter Web
- Spring Boot Starter Security
- MinIO Java SDK
- JJWT (JWT validation)
- Spring Cloud Netflix Eureka Client
- Kotlin Standard Library
- Kotlin Coroutines (optional for async)

## Deployment

### Docker
**Dockerfile**: [`image-service/Dockerfile`](../../image-service/Dockerfile)

**Environment Variables**:
- `MINIO_ENDPOINT` - MinIO server URL
- `MINIO_ACCESS_KEY` - MinIO access key
- `MINIO_SECRET_KEY` - MinIO secret key
- `JWT_SECRET` - JWT signing secret
- `EUREKA_URL` - Eureka server URL

### MinIO Setup
1. MinIO container must be running
2. Access credentials configured
3. Buckets created automatically on first use
4. Network connectivity between service and MinIO

## Monitoring & Health

### Health Checks
- MinIO connectivity check
- Bucket accessibility check
- Disk space monitoring (MinIO)

### Metrics
- Upload success/failure rate
- Average upload time
- Storage usage per bucket
- Download request count

### Logging
- Upload attempts and results
- Token validation failures
- MinIO connection errors
- Image validation failures

## Error Scenarios

### Upload Failures
1. **Invalid Token**: Returns 401, upload rejected
2. **MinIO Unavailable**: Returns 500, retry recommended
3. **Invalid Image Format**: Returns 400, client error
4. **File Too Large**: Returns 413, client error
5. **Bucket Not Accessible**: Returns 500, configuration error

### Download Failures
1. **Image Not Found**: Returns 404
2. **MinIO Unavailable**: Returns 500
3. **Corrupted Image**: Returns 500

## Integration with Other Services

### Music Service
**Flow**:
1. User creates album in Music Service
2. Music Service generates upload token
3. Music Service returns token to client
4. Client uploads artwork to Image Service with token
5. Image Service validates token and stores image

### Gateway
**Routing**: `/api/images/**` → Image Service

### CDN (Future)
- Images served through CDN
- MinIO as origin server
- Cache invalidation on update

## Future Enhancements

- [ ] User avatar support
- [ ] Artist banner images
- [ ] Image resizing and thumbnails
- [ ] Multiple image sizes (small, medium, large)
- [ ] Image optimization (compression, format conversion)
- [ ] CDN integration
- [ ] Image moderation and content filtering
- [ ] Batch upload support
- [ ] Image metadata extraction (EXIF)
- [ ] Signed URLs for private images
- [ ] Image versioning
- [ ] Automatic cleanup of unused images
- [ ] Image analytics (views, downloads)
- [ ] WebP format support
- [ ] Progressive image loading
