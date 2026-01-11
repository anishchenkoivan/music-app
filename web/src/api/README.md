# Frontend API Services

This directory contains all API service modules for the music streaming application. All services are now properly aligned with the backend API endpoints.

## Services Overview

### 1. Authentication Service (`authService.js`)

Handles user authentication and session management.

**Methods:**
- `login(username, password)` - Authenticate user and get JWT token
- `register(userData)` - Create new user account
- `logout()` - Clear authentication token
- `getCurrentUser()` - Get current user's token

**Example:**
```javascript
import { authService } from './api';

// Login
const { token } = await authService.login('username', 'password');

// Register
const userId = await authService.register({
  username: 'johndoe',
  email: 'john@example.com',
  password: 'password123',
  displayName: 'John Doe',
  bio: 'Music lover'
});
```

### 2. User Service (`userService.js`)

Manages user profiles and account information.

**Methods:**
- `createUser(userData)` - Create new user
- `getUser(userId)` - Get user profile
- `updateUser(userId, userData)` - Update user profile
- `getUserIdByCredentials(username, email)` - Get user ID by credentials
- `updatePassword(newPassword)` - Update user password

**Example:**
```javascript
import { userService } from './api';

// Get user profile
const user = await userService.getUser(userId);

// Update profile
await userService.updateUser(userId, {
  displayName: 'New Name',
  bio: 'Updated bio'
});
```

### 3. Music Service (`musicService.js`)

Comprehensive music operations including tracks, albums, artists, and playlists.

**Track Methods:**
- `getTracks(page, size)` - Get paginated tracks
- `getTrack(id)` - Get track details
- `createTrack(title, artistIds, duration)` - Create track (returns trackId and uploadToken)
- `getTracksBatch(trackIds)` - Get multiple tracks by IDs
- `toggleLike(trackId)` - Like/unlike a track

**Album Methods:**
- `getAlbums(page, size)` - Get paginated albums
- `getAlbum(id)` - Get album details
- `createAlbum(artistId, title, tracks)` - Create album (returns album and uploadToken)
- `getArtistAlbums(artistId)` - Get albums by artist

**Artist Methods:**
- `getArtists(page, size)` - Get paginated artists
- `getArtist(id)` - Get artist details
- `createArtist(name, userId)` - Create artist profile
- `getArtistTracks(artistId)` - Get tracks by artist
- `getArtistsByUser(userId)` - Get artists by user

**Playlist Methods:**
- `getPlaylists()` - Get all playlists
- `getPlaylist(id)` - Get playlist details
- `createPlaylist(title, isPublic)` - Create new playlist
- `updatePlaylist(playlistId, title, isPublic)` - Update playlist
- `deletePlaylist(playlistId)` - Delete playlist
- `addTracksToPlaylist(playlistId, trackIds)` - Add tracks to playlist
- `removeTrackFromPlaylist(playlistId, trackId)` - Remove track from playlist

**User Library Methods:**
- `getFavorites()` - Get user's favorite tracks
- `getUserPlaylists()` - Get user's playlists
- `getUserHistory()` - Get user's play history

**Search Methods:**
- `search(query, type, limit)` - Search tracks and artists

**Streaming Methods:**
- `getStreamUrl(trackId)` - Get streaming URL for track

**Example:**
```javascript
import { musicService } from './api';

// Create a track
const { trackId, uploadToken } = await musicService.createTrack(
  'Song Title',
  ['artist-uuid'],
  180
);

// Search
const results = await musicService.search('query', 'track', 20);

// Create playlist
const playlistId = await musicService.createPlaylist('My Playlist', true);

// Add tracks to playlist
await musicService.addTracksToPlaylist(playlistId, [trackId1, trackId2]);

// Like a track
await musicService.toggleLike(trackId);
```

### 4. Image Service (`imageService.js`)

Handles album artwork uploads and retrieval.

**Methods:**
- `uploadArtwork(file, uploadToken)` - Upload album artwork
- `getArtworkUrl(albumId)` - Get artwork URL

**Example:**
```javascript
import { imageService } from './api';

// Upload artwork (requires uploadToken from album creation)
await imageService.uploadArtwork(file, uploadToken);

// Get artwork URL
const artworkUrl = imageService.getArtworkUrl(albumId);
```

### 5. Streaming Service (`streamingService.js`)

Handles audio file uploads and streaming.

**Methods:**
- `uploadAudio(file, trackId, uploadToken)` - Upload audio file
- `getStreamUrl(trackId)` - Get streaming URL

**Example:**
```javascript
import { streamingService } from './api';

// Upload audio (requires uploadToken from track creation)
await streamingService.uploadAudio(audioFile, trackId, uploadToken);

// Get stream URL for playback
const streamUrl = streamingService.getStreamUrl(trackId);
```

### 6. Statistics Service (`statisticsService.js`)

Handles user listening history and statistics.

**Methods:**
- `getUserHistory(userId, limit)` - Get user's listening history

**Example:**
```javascript
import { statisticsService } from './api';

// Get user history
const history = await statisticsService.getUserHistory(userId, 50);
```

## Import Methods

You can import services individually or all at once:

```javascript
// Import individual service
import { authService } from './api/authService';

// Import multiple services
import { authService, musicService } from './api';

// Import axios instance
import { api } from './api';
```

## Key Changes from Previous Implementation

### Fixed Issues:

1. **Authentication Service**
   - ❌ Old: Sent `email: null` in get-id request
   - ✅ New: Only sends provided fields (username or email)

2. **Search Endpoint**
   - ❌ Old: Used `?q=` query parameter
   - ✅ New: Uses `?query=` parameter (matches API spec)

3. **Create Playlist**
   - ❌ Old: Used `name` and `description` fields
   - ✅ New: Uses `title` and `isPublic` fields (matches API spec)

4. **Add Tracks to Playlist**
   - ❌ Old: `POST /playlists/{id}/tracks/{trackId}` (single track)
   - ✅ New: `POST /playlists/{id}/tracks` with `trackIds` array (batch operation)

5. **Favorites/Likes**
   - ❌ Old: Separate `addToFavorites` and `removeFromFavorites` endpoints
   - ✅ New: Single `toggleLike` endpoint using `PUT /tracks/{id}/like`

6. **Track Upload**
   - ❌ Old: Direct multipart upload
   - ✅ New: Two-step process - create track (get uploadToken), then upload audio

7. **Stream URL**
   - ❌ Old: Made GET request to stream endpoint
   - ✅ New: Returns URL string for use in audio player

## Workflow Examples

### Complete Track Upload Workflow

```javascript
import { musicService, streamingService } from './api';

// Step 1: Create track metadata
const { trackId, uploadToken } = await musicService.createTrack(
  'My Song',
  ['artist-uuid'],
  180
);

// Step 2: Upload audio file
await streamingService.uploadAudio(audioFile, trackId, uploadToken);

// Step 3: Track is now available for streaming
const streamUrl = streamingService.getStreamUrl(trackId);
```

### Complete Album Creation Workflow

```javascript
import { musicService, imageService } from './api';

// Step 1: Create album with tracks
const { album, uploadToken } = await musicService.createAlbum(
  'artist-uuid',
  'Album Title',
  [
    { title: 'Track 1', trackDataId: 'track-data-uuid-1' },
    { title: 'Track 2', trackDataId: 'track-data-uuid-2' }
  ]
);

// Step 2: Upload album artwork
await imageService.uploadArtwork(artworkFile, uploadToken);

// Step 3: Album is now complete
const artworkUrl = imageService.getArtworkUrl(album.id);
```

## Error Handling

All services use the axios interceptor for consistent error handling:

- **401 Unauthorized**: Automatically clears token and redirects to login
- **Other errors**: Rejected promise with error details

```javascript
try {
  const track = await musicService.getTrack(trackId);
} catch (error) {
  if (error.response) {
    console.error('API Error:', error.response.data.message);
  } else {
    console.error('Network Error:', error.message);
  }
}
```

## Authentication

The axios instance automatically includes the JWT token from localStorage in all requests. No need to manually add Authorization headers (except for upload operations which use uploadToken).

## Base URL Configuration

The base URL is configured in [`config.js`](../config.js:1) and can be changed via environment variables.
