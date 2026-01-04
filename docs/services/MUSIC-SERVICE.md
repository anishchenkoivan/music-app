# Music Service Documentation

## Overview

The Music Service is the core service managing the music catalog, including tracks, albums, artists, and playlists. It provides full-text search capabilities via ElasticSearch and integrates with Kafka for event-driven updates.

## Technology Stack

- **Languages**: Java, Kotlin
- **Framework**: Spring Boot
- **Database**: PostgreSQL (port 5433)
- **Search Engine**: ElasticSearch (port 9200)
- **Message Queue**: Apache Kafka
- **Build Tool**: Gradle (Kotlin DSL)
- **Migration**: Flyway

## Service Configuration

- **Port**: 8083
- **Service Name**: `music-service`
- **Database**: `music_db`
- **Eureka Registration**: Enabled

## Key Components

### Controllers

#### AlbumController
**Path**: `/albums`

**Endpoints**:
- `POST /albums` - Create new album
- `GET /albums/{id}` - Get album details
- `GET /albums/artist/{artistId}` - Get albums by artist

#### TrackController
**Path**: `/tracks`

**Endpoints**:
- `POST /tracks` - Create new track
- `GET /tracks/{id}` - Get track details
- `POST /tracks/batch` - Get multiple tracks by IDs
- `PUT /tracks/{id}/like` - Like/unlike track
- `GET /tracks/artist/{artistId}` - Get tracks by artist

#### ArtistController
**Path**: `/artists`

**Endpoints**:
- `POST /artists` - Create new artist
- `GET /artists/{id}` - Get artist details
- `GET /artists/user/{userId}` - Get artists by user

#### PlaylistController
**Path**: `/playlists`

**Endpoints**:
- `POST /playlists` - Create playlist
- `GET /playlists/{id}` - Get playlist details
- `PUT /playlists/{id}` - Update playlist
- `DELETE /playlists/{id}` - Delete playlist
- `POST /playlists/{id}/tracks` - Add tracks to playlist
- `DELETE /playlists/{id}/tracks/{trackId}` - Remove track from playlist

#### SearchController
**Path**: `/search`

**Endpoints**:
- `GET /search?query={query}&type={type}` - Search tracks and artists

#### UserMusicController
**Path**: `/user/music`

**Endpoints**:
- `GET /user/music/favorites` - Get user's favorite tracks
- `GET /user/music/playlists` - Get user's playlists
- `GET /user/music/history` - Get user's play history

### Services

#### AlbumService
**Responsibilities**:
- Album creation and management
- Track-album relationship management
- Copyright validation (tracks must belong to album artist)
- Upload token generation for artwork
- Event publishing for album creation

**Key Methods**:
- `createAlbum(AlbumCreateRequest)` - Create album with tracks
- `getAlbumById(UUID)` - Retrieve album details
- `getAlbumsForArtist(UUID)` - Get artist's albums

#### TrackService
**Responsibilities**:
- Track creation and management
- Track data and track view separation
- Like/play count management
- Upload token generation for audio files
- Event publishing for track creation

**Key Methods**:
- `createTrack(TrackCreateRequest)` - Create track with metadata
- `getTrackById(UUID)` - Get track view details
- `likeTrack(UUID, UUID)` - Toggle track like
- `incrementPlayCount(UUID)` - Increment play counter

#### ArtistService
**Responsibilities**:
- Artist profile management
- User-artist relationship
- Artist verification

**Key Methods**:
- `createArtist(ArtistCreateRequest)` - Create artist profile
- `getArtistById(UUID)` - Get artist details
- `getArtistsByUser(UUID)` - Get user's artist profiles

#### PlaylistService
**Responsibilities**:
- Playlist CRUD operations
- Track ordering in playlists
- Special playlists (History, Favorites)
- Public/private playlist management

**Key Methods**:
- `createPlaylist(PlaylistCreateRequest)` - Create new playlist
- `addTracksToPlaylist(UUID, List<UUID>)` - Add tracks
- `removeTrackFromPlaylist(UUID, UUID)` - Remove track
- `getUserPlaylists(UUID)` - Get user's playlists

#### SearchService
**Responsibilities**:
- ElasticSearch integration
- Track and artist indexing
- Full-text search queries
- Search result ranking

**Key Methods**:
- `searchTracks(String query)` - Search tracks
- `searchArtists(String query)` - Search artists
- `indexTrack(Track)` - Index track for search
- `indexArtist(Artist)` - Index artist for search

### Data Model

#### Track Data Entity
**Table**: `track_data`

**Fields**:
- `id` (UUID) - Primary key
- `title` (String) - Track title
- `likes_count` (Long) - Number of likes
- `plays_count` (Long) - Number of plays
- `duration` (Integer) - Duration in seconds
- `is_valid` (Boolean) - Audio file uploaded
- `artists` (Many-to-Many) - Track artists

**Purpose**: Stores actual track metadata and statistics

#### Track View Entity
**Table**: `track_views`

**Fields**:
- `id` (UUID) - Primary key
- `title` (String) - Display title (can differ from track data)
- `track_data_id` (UUID) - Reference to track data
- `album_id` (UUID) - Reference to album
- `album_index` (Integer) - Position in album

**Purpose**: Represents track in context (album, playlist)

#### Album Entity
**Table**: `albums`

**Fields**:
- `id` (UUID) - Primary key
- `title` (String) - Album title
- `artist_id` (UUID) - Primary artist
- `duration` (Integer) - Total duration
- `length` (Integer) - Number of tracks
- `release_date` (Date) - Release date
- `tracks` (One-to-Many) - Track views

#### Artist Entity
**Table**: `artists`

**Fields**:
- `id` (UUID) - Primary key
- `name` (String) - Artist name
- `user_id` (UUID) - Associated user account (nullable)

#### Playlist Entity
**Table**: `playlists`

**Fields**:
- `id` (UUID) - Primary key
- `user_id` (UUID) - Owner
- `title` (String) - Playlist title
- `length` (Integer) - Number of tracks
- `duration` (Integer) - Total duration
- `is_public` (Boolean) - Public visibility
- `is_special` (Boolean) - System playlist
- `special_type` (Enum) - Type (HISTORY, FAVORITES)
- `tracks` (Many-to-Many) - Track views with ordering

### Database Schema

**Migration**: [`V1__create_table.sql`](../../music-service/src/main/resources/db/migration/V1__create_table.sql)

**Key Tables**:
- `track_data` - Track metadata and statistics
- `track_views` - Track instances in albums/playlists
- `albums` - Album information
- `artists` - Artist profiles
- `playlists` - User playlists
- `track_artists` - Many-to-many track-artist relationship
- `playlist_tracks` - Many-to-many playlist-track relationship with ordering

**Constraints**:
- Cascade delete on track data removes track views
- Cascade delete on album removes track views
- Cascade delete on playlist removes playlist tracks
- Check constraints on counts (>= 0) and durations (> 0)

## API Endpoints

### Albums

#### POST /albums
**Description**: Create new album with tracks

**Request Body**:
```json
{
  "artistId": "uuid",
  "generalData": {
    "title": "string",
    "tracks": [
      {
        "title": "string",
        "trackDataId": "uuid"
      }
    ]
  }
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

**Status Codes**:
- `200 OK` - Album created
- `400 BAD REQUEST` - Copyright violation or validation error
- `404 NOT FOUND` - Artist or track not found

---

#### GET /albums/{id}
**Description**: Get album details with tracks

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

### Tracks

#### POST /tracks
**Description**: Create new track

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

**Status Codes**:
- `200 OK` - Track created
- `404 NOT FOUND` - Artist not found

---

#### GET /tracks/{id}
**Description**: Get track details

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

#### PUT /tracks/{id}/like
**Description**: Toggle track like for authenticated user

**Headers**:
- `Authorization: Bearer <jwt-token>`

**Response**: `200 OK`

---

### Artists

#### POST /artists
**Description**: Create artist profile

**Request Body**:
```json
{
  "name": "string",
  "userId": "uuid"
}
```

**Response**: Artist UUID

---

#### GET /artists/{id}
**Description**: Get artist details

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

### Playlists

#### POST /playlists
**Description**: Create new playlist

**Request Body**:
```json
{
  "title": "string",
  "isPublic": true
}
```

**Response**: Playlist UUID

---

#### GET /playlists/{id}
**Description**: Get playlist details

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

#### POST /playlists/{id}/tracks
**Description**: Add tracks to playlist

**Request Body**:
```json
{
  "trackIds": ["uuid"]
}
```

**Response**: `200 OK`

---

### Search

#### GET /search
**Description**: Search tracks and artists

**Query Parameters**:
- `query` (String) - Search query
- `type` (String) - "track" or "artist" (optional, searches both if not specified)
- `limit` (Integer) - Result limit (default: 20)

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

## ElasticSearch Integration

### Indexing Strategy
- **Automatic Indexing**: Tracks and artists indexed on creation
- **Event-Driven**: Kafka events trigger index updates
- **Real-time**: Search results available immediately after creation

### Search Features
- Full-text search on track titles and artist names
- Fuzzy matching for typo tolerance
- Relevance scoring
- Multi-field search (title, artist, album)

### Index Mappings

**Track Index**:
```json
{
  "id": "uuid",
  "title": "text",
  "artistNames": "text[]",
  "albumTitle": "text",
  "duration": "integer",
  "likesCount": "long",
  "playsCount": "long"
}
```

**Artist Index**:
```json
{
  "id": "uuid",
  "name": "text",
  "albumCount": "integer",
  "trackCount": "integer"
}
```

## Kafka Integration

### Events Published

#### TrackCreatedEvent
**Topic**: `track-created`
**Payload**:
```json
{
  "trackId": "uuid",
  "title": "string",
  "artistIds": ["uuid"]
}
```
**Consumers**: Search indexing service

#### AlbumCreatedEvent
**Topic**: `album-created`
**Payload**:
```json
{
  "albumId": "uuid",
  "title": "string"
}
```
**Consumers**: Search indexing service

#### TrackViewCreatedEvent
**Topic**: `track-view-created`
**Payload**:
```json
{
  "trackViewId": "uuid",
  "title": "string"
}
```

### Events Consumed

#### TrackUploadedEvent
**Topic**: `track-uploaded`
**Payload**:
```json
{
  "trackId": "uuid",
  "duration": 0
}
```
**Action**: Mark track as valid, update duration

## Security

### JWT Upload Tokens
- Generated when album/track created
- Short-lived tokens for file upload
- Contains resource ID in claims
- Validated by Image/Streaming services

### Authorization
- Users can only modify their own playlists
- Artists can only create albums for themselves
- Public playlists visible to all
- Private playlists only visible to owner

## Configuration Properties

```properties
server.port=8083
spring.application.name=music-service

# Database
spring.datasource.url=jdbc:postgresql://${MUSIC_SERVICE_DB_HOST:localhost}:${MUSIC_SERVICE_DB_PORT:5433}/music_db
spring.datasource.username=postgres
spring.datasource.password=password

# ElasticSearch
spring.elasticsearch.uris=${ELASTICSEARCH_URL:http://localhost:9200}

# Kafka
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka}
```

## Testing

### Integration Tests
**Location**: `src/test/java/com/musicapp/musicservice/integration/`

**Test Coverage**:
- Track creation and retrieval
- Album creation with copyright validation
- Playlist operations
- Search functionality
- Kafka event publishing
- ElasticSearch indexing

### Test Infrastructure
- Testcontainers for PostgreSQL
- Embedded Kafka for event testing
- ElasticSearch test container
- Mock Auth Service

## Dependencies

### Key Libraries
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Data Elasticsearch
- Spring Kafka
- PostgreSQL Driver
- Flyway Core
- Spring Cloud Netflix Eureka Client
- JJWT (JWT generation)

## Deployment

### Docker
**Dockerfile**: [`music-service/Dockerfile`](../../music-service/Dockerfile)

**Environment Variables**:
- `MUSIC_SERVICE_DB_HOST` - PostgreSQL host
- `MUSIC_SERVICE_DB_PORT` - PostgreSQL port (default: 5433)
- `ELASTICSEARCH_URL` - ElasticSearch URL
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka brokers
- `EUREKA_URL` - Eureka server URL

### Dependencies
- PostgreSQL database
- ElasticSearch cluster
- Kafka cluster
- Auth Service (for token validation)

## Performance Considerations

### Database Optimization
- Indexes on foreign keys
- Composite indexes for common queries
- Connection pooling
- Query optimization for large playlists

### Search Performance
- ElasticSearch caching
- Index sharding for large catalogs
- Async indexing via Kafka

### Caching Strategy
- Consider Redis for frequently accessed tracks/albums
- Cache search results
- Cache artist details

## Future Enhancements

- [ ] Collaborative playlists
- [ ] Track recommendations
- [ ] Genre and mood tagging
- [ ] Advanced search filters
- [ ] Playlist sharing
- [ ] Track comments and reviews
- [ ] Artist verification system
- [ ] Album artwork management
- [ ] Track lyrics support
- [ ] Music charts and trending
