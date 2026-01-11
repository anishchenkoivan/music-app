# Frontend RFC - Part 2: Implementation Details

This is a continuation of the Frontend RFC. See [FRONTEND-RFC.md](./FRONTEND-RFC.md) for the first part.

---

## Component Specifications (Continued)

### 3. SearchBar Component

**Purpose**: Real-time search with debouncing

**Implementation**:
```javascript
import { useState, useEffect } from 'react';
import { useDebounce } from '../hooks/useDebounce';

const SearchBar = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState({ tracks: [], artists: [] });
  const [isLoading, setIsLoading] = useState(false);
  const debouncedQuery = useDebounce(query, 300);

  useEffect(() => {
    if (debouncedQuery.length < 2) {
      setResults({ tracks: [], artists: [] });
      return;
    }

    const searchMusic = async () => {
      setIsLoading(true);
      try {
        const response = await api.get('/search', {
          params: {
            query: debouncedQuery,
            limit: 20
          }
        });
        setResults(response.data);
      } catch (error) {
        console.error('Search error:', error);
      } finally {
        setIsLoading(false);
      }
    };

    searchMusic();
  }, [debouncedQuery]);

  return (
    <div className="search-bar">
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search tracks, artists..."
      />
      {isLoading && <LoadingSpinner />}
      {results.tracks.length > 0 || results.artists.length > 0 ? (
        <SearchResults results={results} />
      ) : null}
    </div>
  );
};
```

### 4. PlaylistManager Component

**Purpose**: Create and manage playlists

**Implementation**:
```javascript
const PlaylistManager = () => {
  const [playlists, setPlaylists] = useState([]);
  const [isCreating, setIsCreating] = useState(false);

  useEffect(() => {
    fetchPlaylists();
  }, []);

  const fetchPlaylists = async () => {
    try {
      const response = await api.get('/user/music/playlists');
      setPlaylists(response.data);
    } catch (error) {
      console.error('Error fetching playlists:', error);
    }
  };

  const createPlaylist = async (title, isPublic) => {
    try {
      const response = await api.post('/playlists/create', {
        title,
        isPublic
      });
      await fetchPlaylists();
      return response.data.playlistId;
    } catch (error) {
      console.error('Error creating playlist:', error);
      throw error;
    }
  };

  const addTrackToPlaylist = async (playlistId, trackId) => {
    try {
      await api.post(`/playlists/${playlistId}/tracks`, {
        trackIds: [trackId]
      });
      // Refresh playlist
      await fetchPlaylists();
    } catch (error) {
      console.error('Error adding track:', error);
      throw error;
    }
  };

  const removeTrackFromPlaylist = async (playlistId, trackId) => {
    try {
      await api.delete(`/playlists/${playlistId}/tracks/${trackId}`);
      await fetchPlaylists();
    } catch (error) {
      console.error('Error removing track:', error);
      throw error;
    }
  };

  return (
    <div className="playlist-manager">
      <button onClick={() => setIsCreating(true)}>
        Create New Playlist
      </button>
      {isCreating && (
        <CreatePlaylistModal
          onClose={() => setIsCreating(false)}
          onCreate={createPlaylist}
        />
      )}
      <div className="playlist-grid">
        {playlists.map(playlist => (
          <PlaylistCard
            key={playlist.id}
            playlist={playlist}
            onAddTrack={addTrackToPlaylist}
            onRemoveTrack={removeTrackFromPlaylist}
          />
        ))}
      </div>
    </div>
  );
};
```

---

## State Management

### Authentication Store (Zustand Example)

```javascript
import create from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      isAuthenticated: false,

      login: async (email, password) => {
        try {
          // Get user ID
          const idResponse = await api.post('/user/get-id', { email });
          const userId = idResponse.data;

          // Get token
          const tokenResponse = await api.post('/auth/get-token', {
            id: userId,
            password
          });
          const token = tokenResponse.data;

          // Get user profile
          const userResponse = await api.get(`/user/${userId}`, {
            headers: { Authorization: `Bearer ${token}` }
          });

          set({
            user: userResponse.data,
            token,
            isAuthenticated: true
          });

          return true;
        } catch (error) {
          console.error('Login error:', error);
          return false;
        }
      },

      register: async (userData) => {
        try {
          const response = await api.post('/user/create-user', userData);
          const userId = response.data;

          // Auto-login after registration
          return await get().login(userData.email, userData.password);
        } catch (error) {
          console.error('Registration error:', error);
          return false;
        }
      },

      logout: () => {
        set({
          user: null,
          token: null,
          isAuthenticated: false
        });
      },

      updateProfile: async (updates) => {
        const { user, token } = get();
        try {
          await api.put(`/user/${user.id}/update`, updates, {
            headers: { Authorization: `Bearer ${token}` }
          });
          
          // Refresh user data
          const userResponse = await api.get(`/user/${user.id}`, {
            headers: { Authorization: `Bearer ${token}` }
          });
          
          set({ user: userResponse.data });
          return true;
        } catch (error) {
          console.error('Update profile error:', error);
          return false;
        }
      }
    }),
    {
      name: 'auth-storage',
      getStorage: () => localStorage
    }
  )
);
```

### Player Store

```javascript
import create from 'zustand';

export const usePlayerStore = create((set, get) => ({
  currentTrack: null,
  queue: [],
  isPlaying: false,
  currentTime: 0,
  duration: 0,
  volume: 1,
  repeat: 'off', // 'off', 'one', 'all'
  shuffle: false,

  playTrack: (track) => {
    set({
      currentTrack: track,
      isPlaying: true
    });
  },

  playQueue: (tracks, startIndex = 0) => {
    set({
      queue: tracks,
      currentTrack: tracks[startIndex],
      isPlaying: true
    });
  },

  playNext: () => {
    const { queue, currentTrack, repeat, shuffle } = get();
    const currentIndex = queue.findIndex(t => t.id === currentTrack?.id);
    
    if (repeat === 'one') {
      // Replay current track
      set({ currentTime: 0 });
      return;
    }

    let nextIndex;
    if (shuffle) {
      nextIndex = Math.floor(Math.random() * queue.length);
    } else {
      nextIndex = currentIndex + 1;
    }

    if (nextIndex >= queue.length) {
      if (repeat === 'all') {
        nextIndex = 0;
      } else {
        set({ isPlaying: false });
        return;
      }
    }

    set({
      currentTrack: queue[nextIndex],
      currentTime: 0
    });
  },

  playPrevious: () => {
    const { queue, currentTrack } = get();
    const currentIndex = queue.findIndex(t => t.id === currentTrack?.id);
    const prevIndex = currentIndex - 1;

    if (prevIndex >= 0) {
      set({
        currentTrack: queue[prevIndex],
        currentTime: 0
      });
    }
  },

  togglePlay: () => {
    set(state => ({ isPlaying: !state.isPlaying }));
  },

  setVolume: (volume) => {
    set({ volume: Math.max(0, Math.min(1, volume)) });
  },

  setCurrentTime: (time) => {
    set({ currentTime: time });
  },

  setDuration: (duration) => {
    set({ duration });
  },

  toggleRepeat: () => {
    const modes = ['off', 'all', 'one'];
    const { repeat } = get();
    const currentIndex = modes.indexOf(repeat);
    const nextIndex = (currentIndex + 1) % modes.length;
    set({ repeat: modes[nextIndex] });
  },

  toggleShuffle: () => {
    set(state => ({ shuffle: !state.shuffle }));
  },

  addToQueue: (track) => {
    set(state => ({
      queue: [...state.queue, track]
    }));
  },

  clearQueue: () => {
    set({ queue: [], currentTrack: null, isPlaying: false });
  }
}));
```

---

## Authentication & Authorization

### Axios Configuration with Interceptors

```javascript
// api/axios.js
import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor - Add auth token
api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      useAuthStore.getState().logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Protected Route Component

```javascript
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

const ProtectedRoute = ({ children, requireArtist = false }) => {
  const { isAuthenticated, user } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requireArtist && !user?.isArtist) {
    return <Navigate to="/become-artist" replace />;
  }

  return children;
};

export default ProtectedRoute;
```

### Route Configuration

```javascript
import { BrowserRouter, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/search" element={<Search />} />
        <Route path="/artist/:id" element={<Artist />} />
        <Route path="/album/:id" element={<Album />} />
        <Route path="/track/:id" element={<Track />} />

        {/* Protected routes */}
        <Route
          path="/library"
          element={
            <ProtectedRoute>
              <Library />
            </ProtectedRoute>
          }
        />
        <Route
          path="/playlists"
          element={
            <ProtectedRoute>
              <Playlists />
            </ProtectedRoute>
          }
        />
        <Route
          path="/history"
          element={
            <ProtectedRoute>
              <History />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />

        {/* Artist-only routes */}
        <Route
          path="/upload"
          element={
            <ProtectedRoute requireArtist>
              <Upload />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}
```

---

## File Upload Flows

### Track Upload with Progress

```javascript
const uploadTrack = async (metadata, audioFile, onProgress) => {
  try {
    // Step 1: Create track metadata
    const metadataResponse = await api.post('/tracks/upload', {
      title: metadata.title,
      artistIds: metadata.artistIds
    });

    const { trackId, uploadToken } = metadataResponse.data;

    // Step 2: Upload audio file
    const formData = new FormData();
    formData.append('file', audioFile);
    formData.append('id', trackId);

    await api.post('/audio/stream/upload', formData, {
      headers: {
        'Authorization': `Bearer ${uploadToken}`,
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total
        );
        onProgress?.(percentCompleted);
      }
    });

    return trackId;
  } catch (error) {
    console.error('Upload error:', error);
    throw error;
  }
};
```

### Album Upload with Artwork

```javascript
const uploadAlbum = async (albumData, artworkFile, onProgress) => {
  try {
    // Step 1: Create album
    const albumResponse = await api.post('/albums', {
      artistId: albumData.artistId,
      generalData: {
        title: albumData.title,
        tracks: albumData.tracks // Array of { title, trackDataId }
      }
    });

    const { album, uploadToken } = albumResponse.data;

    // Step 2: Upload artwork
    if (artworkFile) {
      const formData = new FormData();
      formData.append('file', artworkFile);

      await api.post('/images/artwork/upload', formData, {
        headers: {
          'Authorization': `Bearer ${uploadToken}`,
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          onProgress?.(percentCompleted);
        }
      });
    }

    return album.id;
  } catch (error) {
    console.error('Album upload error:', error);
    throw error;
  }
};
```

### File Validation

```javascript
const validateAudioFile = (file) => {
  const errors = [];

  // Check file type
  if (!file.type.includes('audio/mpeg') && !file.name.endsWith('.mp3')) {
    errors.push('File must be MP3 format');
  }

  // Check file size (max 100MB)
  const maxSize = 100 * 1024 * 1024;
  if (file.size > maxSize) {
    errors.push('File size must be less than 100MB');
  }

  // Check minimum size (100KB)
  const minSize = 100 * 1024;
  if (file.size < minSize) {
    errors.push('File size must be at least 100KB');
  }

  return errors;
};

const validateImageFile = (file) => {
  const errors = [];

  // Check file type
  const validTypes = ['image/jpeg', 'image/png', 'image/webp'];
  if (!validTypes.includes(file.type)) {
    errors.push('Image must be JPEG, PNG, or WebP format');
  }

  // Check file size (max 10MB)
  const maxSize = 10 * 1024 * 1024;
  if (file.size > maxSize) {
    errors.push('Image size must be less than 10MB');
  }

  return errors;
};
```

---

## Audio Streaming

### Audio Player Implementation with Range Requests

```javascript
import { useRef, useState, useEffect } from 'react';

const useAudioPlayer = (trackId) => {
  const audioRef = useRef(null);
  const [state, setState] = useState({
    isPlaying: false,
    currentTime: 0,
    duration: 0,
    buffered: 0,
    isLoading: false,
    error: null
  });

  useEffect(() => {
    if (!trackId) return;

    const audio = new Audio();
    audio.preload = 'metadata';
    audio.src = `/api/stream/${trackId}`;
    audioRef.current = audio;

    // Event listeners
    const handleLoadedMetadata = () => {
      setState(prev => ({
        ...prev,
        duration: audio.duration,
        isLoading: false
      }));
    };

    const handleTimeUpdate = () => {
      setState(prev => ({
        ...prev,
        currentTime: audio.currentTime
      }));
    };

    const handleProgress = () => {
      if (audio.buffered.length > 0) {
        const buffered = audio.buffered.end(audio.buffered.length - 1);
        setState(prev => ({ ...prev, buffered }));
      }
    };

    const handlePlay = () => {
      setState(prev => ({ ...prev, isPlaying: true }));
    };

    const handlePause = () => {
      setState(prev => ({ ...prev, isPlaying: false }));
    };

    const handleEnded = () => {
      setState(prev => ({ ...prev, isPlaying: false }));
      // Trigger next track
    };

    const handleError = (e) => {
      console.error('Audio error:', e);
      setState(prev => ({
        ...prev,
        error: 'Failed to load audio',
        isLoading: false
      }));
    };

    audio.addEventListener('loadedmetadata', handleLoadedMetadata);
    audio.addEventListener('timeupdate', handleTimeUpdate);
    audio.addEventListener('progress', handleProgress);
    audio.addEventListener('play', handlePlay);
    audio.addEventListener('pause', handlePause);
    audio.addEventListener('ended', handleEnded);
    audio.addEventListener('error', handleError);

    setState(prev => ({ ...prev, isLoading: true }));

    return () => {
      audio.pause();
      audio.src = '';
      audio.removeEventListener('loadedmetadata', handleLoadedMetadata);
      audio.removeEventListener('timeupdate', handleTimeUpdate);
      audio.removeEventListener('progress', handleProgress);
      audio.removeEventListener('play', handlePlay);
      audio.removeEventListener('pause', handlePause);
      audio.removeEventListener('ended', handleEnded);
      audio.removeEventListener('error', handleError);
    };
  }, [trackId]);

  const play = () => {
    audioRef.current?.play();
  };

  const pause = () => {
    audioRef.current?.pause();
  };

  const seek = (time) => {
    if (audioRef.current) {
      audioRef.current.currentTime = time;
    }
  };

  const setVolume = (volume) => {
    if (audioRef.current) {
      audioRef.current.volume = Math.max(0, Math.min(1, volume));
    }
  };

  return {
    ...state,
    play,
    pause,
    seek,
    setVolume
  };
};

export default useAudioPlayer;
```

### Progress Bar with Seeking

```javascript
const ProgressBar = ({ currentTime, duration, onSeek }) => {
  const [isDragging, setIsDragging] = useState(false);
  const [dragTime, setDragTime] = useState(0);
  const progressRef = useRef(null);

  const handleMouseDown = (e) => {
    setIsDragging(true);
    updateTime(e);
  };

  const handleMouseMove = (e) => {
    if (isDragging) {
      updateTime(e);
    }
  };

  const handleMouseUp = (e) => {
    if (isDragging) {
      updateTime(e);
      onSeek(dragTime);
      setIsDragging(false);
    }
  };

  const updateTime = (e) => {
    const rect = progressRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const percentage = Math.max(0, Math.min(1, x / rect.width));
    const time = percentage * duration;
    setDragTime(time);
  };

  useEffect(() => {
    if (isDragging) {
      document.addEventListener('mousemove', handleMouseMove);
      document.addEventListener('mouseup', handleMouseUp);
      return () => {
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);
      };
    }
  }, [isDragging]);

  const displayTime = isDragging ? dragTime : currentTime;
  const progress = (displayTime / duration) * 100;

  return (
    <div className="progress-bar-container">
      <span className="time">{formatTime(displayTime)}</span>
      <div
        ref={progressRef}
        className="progress-bar"
        onMouseDown={handleMouseDown}
      >
        <div
          className="progress-fill"
          style={{ width: `${progress}%` }}
        />
        <div
          className="progress-handle"
          style={{ left: `${progress}%` }}
        />
      </div>
      <span className="time">{formatTime(duration)}</span>
    </div>
  );
};

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs.toString().padStart(2, '0')}`;
};
```

---

## Search Implementation

### Debounced Search Hook

```javascript
import { useState, useEffect } from 'react';

export const useDebounce = (value, delay) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};
```

### Search Component with Filters

```javascript
const Search = () => {
  const [query, setQuery] = useState('');
  const [type, setType] = useState('all'); // 'all', 'track', 'artist'
  const [results, setResults] = useState({ tracks: [], artists: [] });
  const [isLoading, setIsLoading] = useState(false);
  const debouncedQuery = useDebounce(query, 300);

  useEffect(() => {
    if (debouncedQuery.length < 2) {
      setResults({ tracks: [], artists: [] });
      return;
    }

    const search = async () => {
      setIsLoading(true);
      try {
        const params = {
          query: debouncedQuery,
          limit: 20
        };
        
        if (type !== 'all') {
          params.type = type;
        }

        const response = await api.get('/search', { params });
        setResults(response.data);
      } catch (error) {
        console.error('Search error:', error);
      } finally {
        setIsLoading(false);
      }
    };

    search();
  }, [debouncedQuery, type]);

  return (
    <div className="search-page">
      <div className="search-header">
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search for tracks, artists..."
          className="search-input"
        />
        <div className="search-filters">
          <button
            className={type === 'all' ? 'active' : ''}
            onClick={() => setType('all')}
          >
            All
          </button>
          <button
            className={type === 'track' ? 'active' : ''}
            onClick={() => setType('track')}
          >
            Tracks
          </button>
          <button
            className={type === 'artist' ? 'active' : ''}
            onClick={() => setType('artist')}
          >
            Artists
          </button>
        </div>
      </div>

      {isLoading && <LoadingSpinner />}

      <div className="search-results">
        {results.tracks.length > 0 && (
          <section>
            <h2>Tracks</h2>
            <TrackList tracks={results.tracks} />
          </section>
        )}

        {results.artists.length > 0 && (
          <section>
            <h2>Artists</h2>
            <ArtistGrid artists={results.artists} />
          </section>
        )}

        {!isLoading && query.length >= 2 &&
         results.tracks.length === 0 &&
         results.artists.length === 0 && (
          <div className="no-results">
            <p>No results found for "{query}"</p>
          </div>
        )}
      </div>
    </div>
  );
};
```

---

## Error Handling

### Global Error Boundary

```javascript
import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    // Log to error reporting service
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-page">
          <h1>Something went wrong</h1>
          <p>{this.state.error?.message}</p>
          <button onClick={() => window.location.reload()}>
            Reload Page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
```

### API Error Handling

```javascript
const handleApiError = (error) => {
  if (error.response) {
    // Server responded with error status
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        return {
          message: 'Invalid request. Please check your input.',
          details: data.message
        };
      case 401:
        return {
          message: 'Authentication required. Please log in.',
          details: 'Your session may have expired'
        };
      case 403:
        return {
          message: 'Access denied.',
          details: 'You don\'t have permission to perform this action'
        };
      case 404:
        return {
          message: 'Resource not found.',
          details: data.message || 'The requested item doesn\'t exist'
        };
      case 413:
        return {
          message: 'File too large.',
          details: 'Please upload a smaller file'
        };
      case 500:
        return {
          message: 'Server error.',
          details: 'Something went wrong on our end. Please try again later.'
        };
      default:
        return {
          message: 'An error occurred.',
          details: data.message || 'Please try again'
        };
    }
  } else if (error.request) {
    // Request made but no response
    return {
      message: 'Network error.',
      details: 'Please check your internet connection'
    };
  } else {
    // Error in request setup
    return {
      message: 'Request error.',
      details: error.message
    };
  }
};
```

### Toast Notification System

```javascript
import { create } from 'zustand';

export const useToastStore = create((set) => ({
  toasts: [],
  
  addToast: (message, type = 'info', duration = 3000) => {
    const id = Date.now();
    set((state) => ({
      toasts: [...state.toasts, { id, message, type, duration }]
    }));
    
    setTimeout(() => {
      set((state) => ({
        toasts: state.toasts.filter((t) => t.id !== id)
      }));
    }, duration);
  },
  
  removeToast: (id) => {
    set((state) => ({
      toasts: state.toasts.filter((t) => t.id !== id)
    }));
  }
}));

// Usage
const { addToast } = useToastStore();
addToast('Track uploaded successfully!', 'success');
addToast('Failed to upload track', 'error');
```

---

## Performance Considerations

### 1. Lazy Loading Components

```javascript
import { lazy, Suspense } from 'react';

const Upload = lazy(() => import('./pages/Upload'));
const Artist = lazy(() => import('./pages/Artist'));
const Album = lazy(() => import('./pages/Album'));

function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <Routes>
        <Route path="/upload" element={<Upload />} />
        <Route path="/artist/:id" element={<Artist />} />
        <Route path="/album/:id" element={<Album />} />
      </Routes>
    </Suspense>
  );
}
```

### 2. Image Optimization

```javascript
const AlbumArtwork = ({ albumId, size = 'medium' }) => {
  const [imageSrc, setImageSrc] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const img = new Image();
    img.src = `/api/images/artwork/${albumId}`;
    img.onload = () => {
      setImageSrc(img.src);
      setIsLoading(false);
    };
    img.onerror = () => {
      setImageSrc('/placeholder-album.png');
      setIsLoading(false);
    };
  }, [albumId]);

  return (
    <div className={`album-artwork ${size}`}>
      {isLoading ? (
        <div className="skeleton" />
      ) : (
        <img src={imageSrc} alt="Album artwork" loading="lazy" />
      )}
    </div>
  );
};
```

### 3. Virtual Scrolling for Large Lists

```javascript
import { FixedSizeList } from 'react