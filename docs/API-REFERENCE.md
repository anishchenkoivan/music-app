# API Reference

## Overview

This document provides a comprehensive reference for all API endpoints in the Music Streaming Application. All requests go through the API Gateway at `http://localhost:8080` (development).

## Base URL

```
Development: http://localhost:8080/api
Production: https://api.yourdomain.com/api
```

## Authentication

Most endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <jwt-token>
```

### Getting a Token

**Endpoint**: `POST /api/auth/get-token`

**Request**:
```json
{
  "id": "user-uuid",
  "password": "password"
}
```

**Response**: JWT token string

---

## Auth Service Endpoints

Base path: `/api/auth`

### POST /api/auth/get-token
Issue JWT token for authentication

**Request Body**:
```json
{
  "id": "uuid",
  "password": "string"
}
```

**Response**: `string` (JWT token)

**Status Codes**:
- `200 OK` - Token issued
- `401 UNAUTHORIZED` - Invalid credentials

---

### POST /api/auth/validate
Validate JWT token (internal use)

**Request Body**:
```json
{
  "token": "jwt-string"
}
```

**Response**:
```json
{
  "id": "uuid",
  "roles": ["USER", "ADMIN"]
}
```

---

### POST /api/auth/create-user
Create user credentials (internal use)

**Request Body**:
```json
{
  "id": "uuid",
  "password": "string"
}
```

**Response**: `200 OK`

---

### PUT /api/auth/update-user
Update user password

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "password": "new-password"
}
```

**Response**: `200 OK`

---

## User Service Endpoints

Base path: `/api/user`

### POST /api/user/create-user
Create new user account

**Request Body**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "displayName": "string",
  "bio": "string"
}
```

**Response**: `uuid` (User ID)

**Status Codes**:
- `200 OK` - User created
- `400 BAD REQUEST` - Validation error

---

### GET /api/user/{id}
Get user details

**Path Parameters**:
- `id` (uuid) - User ID

**Headers** (optional): `Authorization: Bearer <token>`

**Response** (authenticated, own profile):
```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "displayName": "string",
  "bio": "string",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

**Response** (public):
```json
{
  "id": "uuid",
  "username": "string",
  "displayName": "string",
  "bio": "string"
}
```

---

### PUT /api/user/{id}/update
Update user profile

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `id` (uuid) - User ID

**Request Body**:
```json
{
  "displayName": "string",
  "bio": "string"
}
```

**Response**: `200 OK`

---

### POST /api/user/get-id
Get user ID by email or username

**Request Body**:
```json
{
  "email": "string",
  "username": "string"
}
```
*At least one field required*

**Response**: `uuid` (User ID)

---

## Music Service Endpoints

### Albums

Base path: `/api/albums`

#### POST /api/albums/create
Create new album

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "title": "string",
  "tracks": [
    {
      "title": "string",
      "trackDataId": "uuid"
    }
  ]
}
```

**Response**:
```json
{
  "album": {
    "id": "uuid",
    "title": "string",
    "artistId": "uuid",
    "tracks": [...],
    "duration": 0,
    "length": 0
  },
  "uploadToken": "jwt-token"
}
```

**Note**: The artist ID is automatically determined from the authenticated user's artist profile.

---

#### GET /api/albums/{id}
Get album details

**Path Parameters**:
- `id` (uuid) - Album ID

**Response**:
```json
{
  "id": "uuid",
  "title": "string",
  "artistId": "uuid",
  "artistName": "string",
  "tracks": [
    {
      "id": "uuid",
      "title": "string",
      "duration": 0,
      "albumIndex": 0
    }
  ],
  "duration": 0,
  "length": 0,
  "releaseDate": "date"
}
```

---

#### GET /api/artists/{artistId}/albums
Get albums by artist

**Path Parameters**:
- `artistId` (uuid) - Artist ID

**Response**:
```json
{
  "albums": [
    {
      "id": "uuid",
      "title": "string",
      "duration": 0,
      "length": 0
    }
  ]
}
```

---

### Tracks

Base path: `/api/tracks`

#### POST /api/tracks/upload
Create new track

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "title": "string",
  "artistIds": ["uuid"],
  "duration": 0
}
```

**Response**:
```json
{
  "trackId": "uuid",
  "uploadToken": "jwt-token"
}
```

---

#### GET /api/tracks/{id}
Get track details

**Path Parameters**:
- `id` (uuid) - Track ID

**Response**:
```json
{
  "id": "uuid",
  "title": "string",
  "artists": [
    {
      "id": "uuid",
      "name": "string"
    }
  ],
  "duration": 0,
  "likesCount": 0,
  "playsCount": 0,
  "albumId": "uuid",
  "albumTitle": "string"
}
```

---

#### POST /api/tracks/batch
Get multiple tracks by IDs

**Request Body**:
```json
{
  "trackIds": ["uuid"]
}
```

**Response**:
```json
{
  "tracks": [...]
}
```

---

#### POST /api/tracks/{id}/like
Like track

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `id` (uuid) - Track ID

**Response**: `200 OK`

---

#### POST /api/tracks/{id}/unlike
Unlike track

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `id` (uuid) - Track ID

**Response**: `200 OK`

---

#### GET /api/artists/{artistId}/tracks
Get tracks by artist

**Path Parameters**:
- `artistId` (uuid) - Artist ID

**Response**:
```json
{
  "tracks": [...]
}
```

---

### Artists

Base path: `/api/artists`

#### POST /api/artists
Create artist profile

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "name": "string",
  "userId": "uuid"
}
```

**Response**: `uuid` (Artist ID)

---

#### GET /api/artists/{id}
Get artist details

**Path Parameters**:
- `id` (uuid) - Artist ID

**Response**:
```json
{
  "id": "uuid",
  "name": "string",
  "userId": "uuid",
  "albumCount": 0,
  "trackCount": 0
}
```

---

#### GET /api/artists/user/{userId}
Get artists by user

**Path Parameters**:
- `userId` (uuid) - User ID

**Response**:
```json
{
  "artists": [...]
}
```

---

### Playlists

Base path: `/api/playlists`

#### POST /api/playlists/create
Create playlist

**Headers**: `Authorization: Bearer <token>`

**Request Body**:
```json
{
  "title": "string",
  "isPublic": true
}
```

**Response**: `uuid` (Playlist ID)

---

#### GET /api/playlists/{id}
Get playlist details

**Path Parameters**:
- `id` (uuid) - Playlist ID

**Response**:
```json
{
  "id": "uuid",
  "title": "string",
  "userId": "uuid",
  "tracks": [...],
  "length": 0,
  "duration": 0,
  "isPublic": true
}
```

---

#### PUT /api/playlists/{id}/update
Update playlist

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `id` (uuid) - Playlist ID

**Request Body**:
```json
{
  "title": "string",
  "isPublic": true
}
```

**Response**: `200 OK`

---

#### DELETE /api/playlists/{id}
Delete playlist

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `id` (uuid) - Playlist ID

**Response**: `200 OK`

---

#### POST /api/playlists/{id}/tracks
Add tracks to playlist

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `id` (uuid) - Playlist ID

**Request Body**:
```json
{
  "trackIds": ["uuid"]
}
```

**Response**: `200 OK`

---

#### DELETE /api/playlists/{id}/tracks/{trackId}
Remove track from playlist

**Headers**: `Authorization: Bearer <token>`

**Path Parameters**:
- `id` (uuid) - Playlist ID
- `trackId` (uuid) - Track ID

**Response**: `200 OK`

---

### Search

Base path: `/api/search`

#### GET /api/search
Search tracks and artists

**Query Parameters**:
- `query` (string, required) - Search query
- `type` (string, optional) - "track" or "artist"
- `limit` (int, optional) - Result limit (default: 20)

**Response**:
```json
{
  "tracks": [
    {
      "id": "uuid",
      "title": "string",
      "artists": [...],
      "score": 0.95
    }
  ],
  "artists": [
    {
      "id": "uuid",
      "name": "string",
      "score": 0.87
    }
  ]
}
```

---

### User Music

Base path: `/api/user/music`

#### GET /api/user/music/favorites
Get user's favorite tracks

**Headers**: `Authorization: Bearer <token>`

**Response**:
```json
{
  "tracks": [...]
}
```

---

#### GET /api/user/music/playlists
Get user's playlists

**Headers**: `Authorization: Bearer <token>`

**Response**:
```json
{
  "playlists": [...]
}
```

---

#### GET /api/user/music/history
Get user's play history

**Headers**: `Authorization: Bearer <token>`

**Query Parameters**:
- `limit` (int, required) - Number of history entries to return

**Response**:
```json
{
  "userId": "uuid",
  "entries": [
    {
      "trackId": "uuid",
      "playedAt": "timestamp",
      "duration": 180
    }
  ],
  "totalPlays": 1234
}
```

---

## Image Service Endpoints

Base path: `/api/images`

### POST /api/images/artwork/upload
Upload album artwork

**Headers**:
- `Authorization: Bearer <upload-token>`
- `Content-Type: multipart/form-data`

**Request Body**:
- `file` (MultipartFile) - Image file

**Supported Formats**: JPEG, PNG, WebP

**Max Size**: 10 MB

**Response**: `200 OK`

---

### GET /api/images/artwork/{id}
Get album artwork

**Path Parameters**:
- `id` (string) - Album ID

**Response**: Image binary data

**Headers**:
- `Content-Type: image/jpeg` (or appropriate)

---

## Streaming Service Endpoints

Base path: `/api/stream`

### GET /api/stream/{id}
Stream audio file

**Path Parameters**:
- `id` (string) - Track ID

**Headers** (optional):
- `Range: bytes={start}-{end}`

**Response**: Audio binary stream

**Headers**:
- `Content-Type: audio/mpeg`
- `Accept-Ranges: bytes`
- `Content-Range: bytes {start}-{end}/{total}` (if range requested)

**Status Codes**:
- `200 OK` - Full stream
- `206 PARTIAL CONTENT` - Range stream

---

### POST /api/stream/upload
Upload audio file

**Headers**:
- `Authorization: Bearer <upload-token>`
- `Content-Type: multipart/form-data`

**Request Body**:
- `file` (MultipartFile) - MP3 file
- `id` (string) - Track ID

**Supported Format**: MP3 only

**Max Size**: 100 MB

**Response**: `200 OK`

---

## Statistics Service Endpoints

Base path: `/api/history`

### GET /api/history/for-user/{userId}
Get user's listening history

**Path Parameters**:
- `userId` (uuid) - User ID

**Query Parameters**:
- `limit` (int, optional) - Number of entries (default: 10, max: 100)

**Response**:
```json
{
  "userId": "uuid",
  "entries": [
    {
      "trackId": "uuid",
      "playedAt": "timestamp",
      "duration": 180
    }
  ],
  "totalPlays": 1234
}
```

---

## Error Responses

All endpoints may return error responses in the following format:

```json
{
  "message": "Error description",
  "timestamp": "2024-01-04T19:30:00Z",
  "path": "/api/endpoint"
}
```

### Common Status Codes

- `200 OK` - Request successful
- `201 CREATED` - Resource created
- `204 NO CONTENT` - Success with no response body
- `400 BAD REQUEST` - Invalid request data
- `401 UNAUTHORIZED` - Authentication required or failed
- `403 FORBIDDEN` - Insufficient permissions
- `404 NOT FOUND` - Resource not found
- `409 CONFLICT` - Resource conflict (e.g., duplicate)
- `413 PAYLOAD TOO LARGE` - File size exceeds limit
- `416 RANGE NOT SATISFIABLE` - Invalid byte range
- `500 INTERNAL SERVER ERROR` - Server error

---

## Rate Limiting

Rate limiting is not currently implemented but recommended for production:

- **Anonymous**: 100 requests/minute
- **Authenticated**: 1000 requests/minute
- **Upload**: 10 uploads/hour
- **Search**: 60 requests/minute

---

## Pagination

For endpoints returning lists, pagination parameters:

- `page` (int) - Page number (0-indexed)
- `size` (int) - Items per page (default: 20, max: 100)
- `sort` (string) - Sort field and direction (e.g., "createdAt,desc")

**Response includes**:
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

---

## Webhooks (Future)

Planned webhook support for:
- Track upload completed
- Album published
- User followed
- Playlist shared

---

## API Versioning

Current version: `v1` (implicit)

Future versions will use URL versioning:
- `/api/v1/...`
- `/api/v2/...`

---

## SDKs and Client Libraries

Official SDKs (planned):
- JavaScript/TypeScript
- Python
- Java
- Swift (iOS)
- Kotlin (Android)

---

## OpenAPI/Swagger

Interactive API documentation available at:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI spec:
```
http://localhost:8080/v3/api-docs
```

*(Note: Swagger integration needs to be added)*

---

## Testing the API

### Using cURL

**Get Token**:
```bash
curl -X POST http://localhost:8080/api/auth/get-token \
  -H "Content-Type: application/json" \
  -d '{"id":"user-uuid","password":"password"}'
```

**Create User**:
```bash
curl -X POST http://localhost:8080/api/user/create-user \
  -H "Content-Type: application/json" \
  -d '{
    "username":"johndoe",
    "email":"john@example.com",
    "password":"password123",
    "displayName":"John Doe"
  }'
```

**Get Track**:
```bash
curl http://localhost:8080/api/tracks/{track-id}
```

**Stream Audio**:
```bash
curl -H "Range: bytes=0-1023" \
  http://localhost:8080/api/stream/{track-id} \
  -o audio-chunk.mp3
```

### Using Postman

Import the Postman collection (to be created):
```
docs/postman/music-app-api.json
```

---

## Best Practices

### Authentication
- Always use HTTPS in production
- Store tokens securely (not in localStorage)
- Implement token refresh mechanism
- Handle 401 responses by re-authenticating

### File Uploads
- Use multipart/form-data
- Validate file types client-side
- Show upload progress
- Handle upload failures gracefully

### Streaming
- Use Range requests for seeking
- Implement buffering strategy
- Handle network interruptions
- Cache frequently played tracks

### Error Handling
- Always check status codes
- Parse error messages
- Implement retry logic for transient failures
- Log errors for debugging

### Performance
- Batch requests when possible
- Use pagination for large lists
- Implement client-side caching
- Compress request/response data
