# Frontend RFC - Music Streaming Application

**RFC Number**: 001  
**Title**: Frontend Architecture and API Integration Specification  
**Author**: System Architect  
**Status**: Draft  
**Created**: 2026-01-11  
**Last Updated**: 2026-01-11

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [System Overview](#system-overview)
3. [API Endpoints Reference](#api-endpoints-reference)
4. [User Flows](#user-flows)
5. [Frontend Architecture](#frontend-architecture)
6. [Component Specifications](#component-specifications)
7. [State Management](#state-management)
8. [Authentication & Authorization](#authentication--authorization)
9. [File Upload Flows](#file-upload-flows)
10. [Audio Streaming](#audio-streaming)
11. [Search Implementation](#search-implementation)
12. [Error Handling](#error-handling)
13. [Performance Considerations](#performance-considerations)
14. [Security Considerations](#security-considerations)
15. [Testing Strategy](#testing-strategy)
16. [Deployment](#deployment)

---

## Executive Summary

This RFC defines the frontend architecture for a music streaming application that interfaces with a microservices backend. The application supports:

- **User Management**: Registration, authentication, profile management
- **Music Upload**: Artists can upload tracks and create albums
- **Music Discovery**: Search, browse artists, albums, and tracks
- **Playback**: Stream audio with range-based requests
- **Playlists**: Create, manage, and share playlists
- **Social Features**: Like tracks, view listening history

**Base API URL**: `http://localhost:8080/api` (development)

---

## System Overview

### Backend Architecture

The backend consists of 8 microservices accessed through an API Gateway:

- **Gateway** (Port 8080): Single entry point, routes to services
- **Auth Service** (Port 8081): JWT authentication
- **User Service** (Port 8082): User profiles
- **Music Service** (Port 8083): Catalog, playlists, search
- **Image Service** (Port 8084): Album artwork storage
- **Streaming Service** (Port 8085): Audio file streaming
- **Statistics Service** (Port 8086): Listening history
- **Eureka** (Port 8761): Service discovery

### Technology Stack

**Backend**:
- Java 17, Kotlin
- Spring Boot, Spring Cloud
- PostgreSQL, ClickHouse, ElasticSearch
- MinIO (S3-compatible storage)
- Apache Kafka

**Recommended Frontend Stack**:
- React 18+ or Vue 3+
- TypeScript
- Axios for HTTP requests
- React Query / TanStack Query for data fetching
- Zustand or Redux for state management
- Tailwind CSS or Material-UI
- Howler.js or native Audio API for playback

---

## API Endpoints Reference

### Authentication & User Management

#### 1. Create User Account
```http
POST /api/user/create-user
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string",
  "displayName": "string",
  "bio": "string"
}

Response: "user-uuid"
Status: 200 OK | 400 Bad Request
```

**Validation Rules**:
- Username: 3-50 chars, alphanumeric + underscore
- Email: Valid email format
- Password: Minimum 8 characters
- Display Name: Max 100 chars
- Bio: Max 500 chars

#### 2. Get Authentication Token
```http
POST /api/auth/get-token
Content-Type: application/json

{
  "id": "user-uuid",
  "password": "string"
}

Response: "jwt-token-string"
Status: 200 OK | 401 Unauthorized
```

**Token Details**:
- Expiration: 7 days (604800 seconds)
- Format: JWT with HMAC-SHA signature
- Claims: user ID, roles, issued at, expiration

#### 3. Get User Profile
```http
GET /api/user/{userId}
Authorization: Bearer {token} (optional)

Response (authenticated, own profile):
{
  "firstName": "string",
  "lastName": "string",
  "username": "string",
  "email": "string",
  "bio": "string",
  "country": "string",
  "profilePicture": "string"
}

Response (public):
{
  "username": "string",
  "bio": "string"
}

Status: 200 OK | 404 Not Found
```

#### 4. Update User Profile
```http
PUT /api/user/{userId}/update
Authorization: Bearer {token}
Content-Type: application/json

{
  "displayName": "string",
  "bio": "string"
}

Status: 200 OK | 401 Unauthorized | 404 Not Found
```

#### 5. Get User ID by Email/Username
```http
POST /api/user/get-id
Content-Type: application/json

{
  "email": "string",
  "username": "string"
}

Response: "user-uuid"
Status: 200 OK | 404 Not Found
```

---

### Music Catalog

#### 6. Upload Track (Create Track Data)
```http
POST /api/tracks/upload
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "string",
  "artistIds": ["uuid"]
}

Response:
{
  "trackId": "uuid",
  "uploadToken": "jwt-token"
}

Status: 200 OK | 401 Unauthorized | 404 Not Found (artist)
```

**Flow**:
1. Create track metadata
2. Receive track ID and upload token
3. Use upload token to upload MP3 file
4. Track becomes valid after file upload

#### 7. Upload Audio File
```http
POST /api/audio/stream/upload
Authorization: Bearer {upload-token}
Content-Type: multipart/form-data

file: (MP3 file)
id: "track-id"

Status: 200 OK | 400 Bad Request | 401 Unauthorized
```

**Requirements**:
- Format: MP3 only
- Max size: 100 MB
- Min size: 100 KB
- Upload token from track creation

#### 8. Get Track Details
```http
GET /api/tracks/{trackId}

Response:
{
  "id": "uuid",
  "title": "string",
  "artists": [
    {
      "id": "uuid",
      "name": "string"
    }
  ],
  "duration": 180,
  "likesCount": 0,
  "playsCount": 0,
  "albumId": "uuid",
  "albumTitle": "string"
}

Status: 200 OK | 404 Not Found
```

#### 9. Like/Unlike Track
```http
POST /api/tracks/{trackId}/like
Authorization: Bearer {token}

Status: 200 OK | 401 Unauthorized

POST /api/tracks/{trackId}/unlike
Authorization: Bearer {token}

Status: 200 OK | 401 Unauthorized
```

#### 10. Create Album
```http
POST /api/albums
Authorization: Bearer {token}
Content-Type: application/json

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

Response:
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

Status: 200 OK | 400 Bad Request | 404 Not Found
```

**Copyright Validation**:
- All tracks must belong to the album's artist
- Prevents unauthorized track usage

#### 11. Upload Album Artwork
```http
POST /api/images/artwork/upload
Authorization: Bearer {upload-token}
Content-Type: multipart/form-data

file: (image file)

Status: 200 OK | 400 Bad Request | 401 Unauthorized
```

**Requirements**:
- Formats: JPEG, PNG, WebP
- Max size: 10 MB
- Min size: 1 KB

#### 12. Get Album Details
```http
GET /api/albums/{albumId}

Response:
{
  "id": "uuid",
  "title": "string",
  "artistId": "uuid",
  "artistName": "string",
  "tracks": [
    {
      "id": "uuid",
      "title": "string",
      "duration": 180,
      "albumIndex": 0
    }
  ],
  "duration": 0,
  "length": 0,
  "releaseDate": "2024-01-01"
}

Status: 200 OK | 404 Not Found
```

#### 13. Get Album Artwork
```http
GET /api/images/artwork/{albumId}

Response: Binary image data
Content-Type: image/jpeg | image/png | image/webp

Status: 200 OK | 404 Not Found
```

#### 14. Get Artist Details
```http
GET /api/artists/{artistId}

Response:
{
  "id": "uuid",
  "name": "string",
  "userId": "uuid",
  "albumCount": 0,
  "trackCount": 0
}

Status: 200 OK | 404 Not Found
```

#### 15. Get Artist's Albums
```http
GET /api/artists/{artistId}/albums

Response:
{
  "artistId": "uuid",
  "albums": [...]
}

Status: 200 OK | 404 Not Found
```

#### 16. Get Artist's Tracks
```http
GET /api/artists/{artistId}/tracks

Response:
{
  "artistId": "uuid",
  "tracks": [...]
}

Status: 200 OK | 404 Not Found
```

#### 17. Get User's Artists
```http
GET /api/artists/user/{userId}

Response:
{
  "id": "uuid",
  "name": "string",
  "userId": "uuid"
}

Status: 200 OK | 404 Not Found
```

---

### Playlists

#### 18. Create Playlist
```http
POST /api/playlists/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "string",
  "isPublic": true
}

Response:
{
  "playlistId": "uuid"
}

Status: 200 OK | 401 Unauthorized
```

#### 19. Get Playlist Details
```http
GET /api/playlists/{playlistId}
Authorization: Bearer {token}

Response:
{
  "id": "uuid",
  "title": "string",
  "userId": "uuid",
  "tracks": [...],
  "length": 0,
  "duration": 0,
  "isPublic": true
}

Status: 200 OK | 401 Unauthorized | 404 Not Found
```

#### 20. Update Playlist
```http
PUT /api/playlists/{playlistId}/update
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "string",
  "isPublic": true
}

Status: 200 OK | 401 Unauthorized | 404 Not Found
```

#### 21. Add Tracks to Playlist
```http
POST /api/playlists/{playlistId}/tracks
Authorization: Bearer {token}
Content-Type: application/json

{
  "trackIds": ["uuid"]
}

Status: 200 OK | 401 Unauthorized | 404 Not Found
```

#### 22. Remove Track from Playlist
```http
DELETE /api/playlists/{playlistId}/tracks/{trackId}
Authorization: Bearer {token}

Status: 200 OK | 401 Unauthorized | 404 Not Found
```

#### 23. Get User's Playlists
```http
GET /api/user/music/playlists
Authorization: Bearer {token}

Response: [
  {
    "id": "uuid",
    "title": "string",
    "length": 0,
    "duration": 0,
    "isPublic": true
  }
]

Status: 200 OK | 401 Unauthorized
```

---

### Audio Streaming

#### 24. Stream Audio
```http
GET /api/stream/{trackId}
Range: bytes={start}-{end} (optional)

Response: Audio binary stream
Content-Type: audio/mpeg
Accept-Ranges: bytes
Content-Range: bytes {start}-{end}/{total}

Status: 200 OK | 206 Partial Content | 404 Not Found | 416 Range Not Satisfiable
```

**Range Request Examples**:
```
Range: bytes=0-1023        # First 1KB
Range: bytes=1024-2047     # Second 1KB
Range: bytes=0-            # From start to end
Range: bytes=-1024         # Last 1KB
```

**Benefits**:
- Progressive loading
- Seeking support
- Bandwidth optimization
- Resume interrupted playback

---

### Search

#### 25. Search Tracks and Artists
```http
GET /api/search?query={query}&type={type}&limit={limit}

Query Parameters:
- query: Search string (required)
- type: "track" | "artist" (optional, searches both if omitted)
- limit: Result limit (default: 20)

Response:
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

Status: 200 OK
```

**Search Features**:
- Full-text search via ElasticSearch
- Fuzzy matching for typos
- Relevance scoring
- Multi-field search

---

### User Music Library

#### 26. Get Favorite Tracks
```http
GET /api/user/music/favorites
Authorization: Bearer {token}

Response: [
  {
    "id": "uuid",
    "title": "string",
    "artists": [...]
  }
]

Status: 200 OK | 401 Unauthorized
```

#### 27. Get Listening History
```http
GET /api/user/music/history?limit={limit}
Authorization: Bearer {token}

Query Parameters:
- limit: Number of entries (default: 10, max: 100)

Response:
{
  "userId": "uuid",
  "entries": [
    {
      "trackId": "uuid",
      "playedAt": "2024-01-04T19:30:00Z",
      "duration": 180
    }
  ],
  "totalPlays": 1234
}

Status: 200 OK | 401 Unauthorized
```

---

## User Flows

### 1. Registration & Login Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Registration Flow                         │
└─────────────────────────────────────────────────────────────┘

1. User fills registration form
   ├─ Username (unique)
   ├─ Email (unique)
   ├─ Password (min 8 chars)
   ├─ Display Name
   └─ Bio (optional)

2. Frontend validates input
   └─ Client-side validation

3. POST /api/user/create-user
   └─ Backend creates user in User Service
   └─ Backend creates credentials in Auth Service
   └─ Returns user UUID

4. Automatically log in user
   └─ POST /api/auth/get-token with UUID and password
   └─ Store JWT token in localStorage/sessionStorage
   └─ Redirect to home/dashboard

┌─────────────────────────────────────────────────────────────┐
│                      Login Flow                              │
└─────────────────────────────────────────────────────────────┘

1. User enters email/username and password

2. Frontend gets user ID
   └─ POST /api/user/get-id with email or username
   └─ Receives user UUID

3. Frontend requests token
   └─ POST /api/auth/get-token with UUID and password
   └─ Receives JWT token

4. Store token and fetch user profile
   └─ Store JWT in localStorage
   └─ GET /api/user/{userId} with token
   └─ Store user data in state
   └─ Redirect to home/dashboard
```

### 2. Track Upload Flow (Artist)

```
┌─────────────────────────────────────────────────────────────┐
│                   Track Upload Flow                          │
└─────────────────────────────────────────────────────────────┘

Prerequisites:
- User must be authenticated
- User must have an artist profile (auto-created on first upload)

Step 1: Create Track Metadata
├─ User fills form:
│  ├─ Track title
│  ├─ Select artists (can be multiple)
│  └─ Optional: Genre, description
│
├─ POST /api/tracks/upload
│  └─ Request: { title, artistIds: [uuid] }
│  └─ Response: { trackId, uploadToken }
│
└─ Store trackId and uploadToken

Step 2: Upload MP3 File
├─ User selects MP3 file
│  └─ Validate: MP3 format, max 100MB
│
├─ Show upload progress
│
├─ POST /api/audio/stream/upload
│  ├─ Authorization: Bearer {uploadToken}
│  ├─ Content-Type: multipart/form-data
│  ├─ Body: file + id (trackId)
│  └─ Backend validates MP3, extracts duration
│
├─ Backend publishes TrackUploadedEvent to Kafka
│
├─ Music Service marks track as valid
│
└─ Track is now available for streaming

Step 3: Optional - Create Album
├─ User can add track to new or existing album
│
├─ POST /api/albums
│  └─ Request: { artistId, generalData: { title, tracks: [...] } }
│  └─ Response: { album, uploadToken }
│
├─ Upload album artwork
│  └─ POST /api/images/artwork/upload
│  └─ Authorization: Bearer {uploadToken}
│  └─ Image file (JPEG/PNG/WebP, max 10MB)
│
└─ Album is complete with artwork

Success State:
└─ Track is searchable, playable, and appears in artist's tracks
```

### 3. Music Discovery & Playback Flow

```
┌─────────────────────────────────────────────────────────────┐
│              Music Discovery & Playback Flow                 │
└─────────────────────────────────────────────────────────────┘

Discovery Methods:

1. Search
   ├─ User types in search box
   ├─ GET /api/search?query={query}
   ├─ Display results: tracks and artists
   └─ Click to play or view details

2. Browse Artists
   ├─ GET /api/artists/{artistId}
   ├─ GET /api/artists/{artistId}/albums
   ├─ GET /api/artists/{artistId}/tracks
   └─ Display artist profile with discography

3. Browse Albums
   ├─ GET /api/albums/{albumId}
   ├─ Display track list with artwork
   └─ Play album or individual tracks

Playback Flow:

1. User clicks play on track
   └─ GET /api/tracks/{trackId} to get track details

2. Initialize audio player
   ├─ Create Audio element or use Howler.js
   ├─ Set source: /api/stream/{trackId}
   └─ Enable range requests for seeking

3. Stream audio with range requests
   ├─ Initial request: Range: bytes=0-
   ├─ For seeking: Range: bytes={position}-
   └─ Progressive loading

4. Track playback events
   ├─ On play: Record start time
   ├─ On pause: Calculate duration played
   ├─ On end: Send play history event
   └─ Update play count

5. Send listening history (optional)
   └─ Backend publishes to Kafka
   └─ Statistics Service stores in ClickHouse

6. Update UI
   ├─ Show current time / duration
   ├─ Update progress bar
   ├─ Display track info and artwork
   └─ Show play/pause, next, previous controls
```

### 4. Playlist Management Flow

```
┌─────────────────────────────────────────────────────────────┐
│                 Playlist Management Flow                     │
└─────────────────────────────────────────────────────────────┘

Create Playlist:
1. User clicks "Create Playlist"
2. Enter playlist name and visibility (public/private)
3. POST /api/playlists/create
   └─ Request: { title, isPublic }
   └─ Response: { playlistId }
4. Redirect to playlist page

Add Tracks to Playlist:
1. User browses music (search, albums, etc.)
2. Click "Add to Playlist" on track
3. Select target playlist from list
4. POST /api/playlists/{playlistId}/tracks
   └─ Request: { trackIds: [uuid] }
5. Show success notification
6. Update playlist UI

Manage Playlist:
1. View playlist
   └─ GET /api/playlists/{playlistId}
   └─ Display tracks with artwork

2. Reorder tracks (drag & drop)
   └─ Update local state
   └─ PUT /api/playlists/{playlistId}/update

3. Remove track
   └─ DELETE /api/playlists/{playlistId}/tracks/{trackId}
   └─ Update UI

4. Edit playlist details
   └─ PUT /api/playlists/{playlistId}/update
   └─ Update title or visibility

5. Play playlist
   └─ Load all tracks into queue
   └─ Start playback from first track
   └─ Auto-advance to next track

Special Playlists:
├─ Favorites: Auto-managed, tracks added via like button
├─ History: Auto-managed, recent plays
└─ User-created: Manual management
```

### 5. Social Features Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Social Features Flow                      │
└─────────────────────────────────────────────────────────────┘

Like/Unlike Track:
1. User clicks heart icon on track
2. POST /api/tracks/{trackId}/like
3. Update UI (filled heart, increment count)
4. Track added to Favorites playlist
5. To unlike: POST /api/tracks/{trackId}/unlike

View Listening History:
1. Navigate to "History" page
2. GET /api/user/music/history?limit=50
3. Display recent plays with timestamps
4. Click track to play again
5. Pagination for older history

View Favorites:
1. Navigate to "Favorites" page
2. GET /api/user/music/favorites
3. Display liked tracks
4. Play or manage favorites

Profile Viewing:
1. Click on artist or user name
2. GET /api/user/{userId} (public view)
3. Display profile info
4. Show public playlists (if implemented)
5. Show artist's music if artist profile
```

---

## Frontend Architecture

### Recommended Structure

```
src/
├── api/
│   ├── axios.js              # Axios instance with interceptors
│   ├── auth.js               # Authentication API calls
│   ├── user.js               # User API calls
│   ├── music.js              # Music catalog API calls
│   ├── playlist.js           # Playlist API calls
│   ├── search.js             # Search API calls
│   └── upload.js             # File upload utilities
│
├── components/
│   ├── auth/
│   │   ├── LoginForm.jsx
│   │   ├── RegisterForm.jsx
│   │   └── ProtectedRoute.jsx
│   │
│   ├── player/
│   │   ├── AudioPlayer.jsx
│   │   ├── PlayerControls.jsx
│   │   ├── ProgressBar.jsx
│   │   ├── VolumeControl.jsx
│   │   └── Queue.jsx
│   │
│   ├── music/
│   │   ├── TrackList.jsx
│   │   ├── TrackItem.jsx
│   │   ├── AlbumCard.jsx
│   │   ├── AlbumGrid.jsx
│   │   ├── ArtistCard.jsx
│   │   └── ArtistProfile.jsx
│   │
│   ├── playlist/
│   │   ├── PlaylistCard.jsx
│   │   ├── PlaylistView.jsx
│   │   ├── CreatePlaylist.jsx
│   │   └── AddToPlaylist.jsx
│   │
│   ├── upload/
│   │   ├── TrackUpload.jsx
│   │   ├── AlbumUpload.jsx
│   │   ├── FileUploader.jsx
│   │   └── UploadProgress.jsx
│   │
│   ├── search/
│   │   ├── SearchBar.jsx
│   │   ├── SearchResults.jsx
│   │   └── SearchFilters.jsx
│   │
│   └── common/
│       ├── Header.jsx
│       ├── Sidebar.jsx
│       ├── Loading.jsx
│       ├── ErrorBoundary.jsx
│       └── Toast.jsx
│
├── pages/
│   ├── Home.jsx
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Search.jsx
│   ├── Artist.jsx
│   ├── Album.jsx
│   ├── Playlist.jsx
│   ├── Library.jsx
│   ├── Upload.jsx
│   ├── Profile.jsx
│   └── History.jsx
│
├── hooks/
│   ├── useAuth.js
│   ├── usePlayer.js
│   ├── usePlaylist.js
│   ├── useSearch.js
│   └── useUpload.js
│
├── store/
│   ├── authStore.js          # Authentication state
│   ├── playerStore.js        # Player state
│   ├── playlistStore.js      # Playlist state
│   └── uiStore.js            # UI state
│
├── utils/
│   ├── formatTime.js
│   ├── formatBytes.js
│   ├── validators.js
│   └── constants.js
│
├── styles/
│   └── global.css
│
├── App.jsx
└── main.jsx
```

---

## Component Specifications

### 1. AudioPlayer Component

**Purpose**: Core audio playback component with range-based streaming

**Props**:
```typescript
interface AudioPlayerProps {
  trackId: string;
  autoPlay?: boolean;
  onEnded?: () => void;
  onPlay?: () => void;
  onPause?: () => void;
}
```

**State**:
```typescript
interface PlayerState {
  isPlaying: boolean;
  currentTime: number;
  duration: number;
  volume: number;
  isMuted: boolean;
  isLoading: boolean;
  error: string | null;
}
```

**Implementation**:
```javascript
// Using native Audio API with range requests
const AudioPlayer = ({ trackId, autoPlay = false }) => {
  const audioRef = useRef(new Audio());
  const [state, setState] = useState({
    isPlaying: false,
    currentTime: 0,
    duration: 0,
    volume: 1,
    isMuted: false,
    isLoading: false,
    error: null
  });

  useEffect(() => {
    const audio = audioRef.current;
    audio.src = `/api/stream/${trackId}`;
    
    // Enable range requests for seeking
    audio.preload = 'metadata';
    
    if (autoPlay) {
      audio.play();
    }

    // Event listeners
    audio.addEventListener('loadedmetadata', handleLoadedMetadata);
    audio.addEventListener('timeupdate', handleTimeUpdate);
    audio.addEventListener('ended', handleEnded);
    audio.addEventListener('error', handleError);

    return () => {
      audio.pause();
      audio.src = '';
      // Remove event listeners
    };
  }, [trackId]);

  const handleSeek = (time) => {
    audioRef.current.currentTime = time;
  };

  const handlePlayPause = () => {
    if (state.isPlaying) {
      audioRef.current.pause();
    } else {
      audioRef.current.play();
    }
  };

  return (
    <div className="audio-player">
      <PlayerControls
        isPlaying={state.isPlaying}
        onPlayPause={handlePlayPause}
      />
      <ProgressBar
        currentTime={state.currentTime}
        duration={state.duration}
        onSeek={handleSeek}
      />
      <VolumeControl
        volume={state.volume}
        isMuted={state.isMuted}
        onVolumeChange={handleVolumeChange}
      />
    </div>
  );
};
```

### 2. TrackUpload Component

**Purpose**: Handle track metadata and audio file upload

**Implementation**:
```javascript
const TrackUpload = () => {
  const [step, setStep] = useState(1); // 1: metadata, 2: file upload
  const [trackData, setTrackData] = useState({
    title: '',
    artistIds: []
  });
  const [uploadToken, setUploadToken] = useState(null);
  const [trackId, setTrackId] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);

  // Step 1: Create track metadata
  const handleCreateTrack = async () => {
    try {
      const response = await api.post('/tracks/upload', trackData);
      setTrackId(response.data.trackId);
      setUploadToken(response.data.uploadToken);
      setStep(2);
    } catch (error) {
      // Handle error
    }
  };

  // Step 2: Upload MP3 file
  const handleFileUpload = async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('id', trackId);

    try {
      await api.post('/audio/stream/upload', formData, {
        headers: {
          'Authorization': `Bearer ${uploadToken}`,
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: (progressEvent) => {
          const progress = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          setUploadProgress(progress);
        }
      });
      
      // Success - track is now available
      navigate(`/track/${trackId}`);
    } catch (error) {
      // Handle error
    }
  };

  return (
    <div className="track-upload">
      {step === 1 && (
        <TrackMetadataForm
          data={trackData}
          onChange={setTrackData}
          onSubmit={handleCreateTrack}
        />
      )}
      {step === 2 && (
        <FileUploader
          accept=".mp3"
          maxSize={100 * 1024 * 1024} // 100MB
          onUpload={handleFileUpload}
          progress={uploadProgress}
        />
      )}
    </div>
  );
};
```

### 3. SearchBar Component

**Purpose**: Real-time search with debouncing

**Implementation**:
```javascript
const SearchBar = () => {
  const [query, setQuery] = useState('');
  const [results, setResults]