# Frontend RFC - Complete Summary & Quick Reference

**Complete Documentation**: This RFC is split into 3 parts due to size:
- **[Part 1: Overview & API Reference](./FRONTEND-RFC.md)** - System overview, all API endpoints, user flows
- **[Part 2: Implementation Details](./FRONTEND-RFC-PART2.md)** - Component specs, state management, file uploads
- **[Part 3: Security, Testing & Deployment](./FRONTEND-RFC-PART3.md)** - Security, testing strategies, deployment

---

## Quick Start Guide

### Prerequisites
- Node.js 18+
- Backend services running on `http://localhost:8080`
- Modern browser with ES6+ support

### Setup
```bash
# Create React app with Vite
npm create vite@latest music-app-frontend -- --template react

cd music-app-frontend
npm install

# Install dependencies
npm install axios zustand react-router-dom
npm install -D @testing-library/react @testing-library/jest-dom vitest
```

### Project Structure
```
src/
├── api/           # API client and endpoints
├── components/    # Reusable components
├── pages/         # Route pages
├── hooks/         # Custom hooks
├── store/         # State management (Zustand)
├── utils/         # Utilities and helpers
└── App.jsx        # Main app component
```

---

## Core API Endpoints Summary

### Authentication
| Method | Endpoint | Purpose | Auth Required |
|--------|----------|---------|---------------|
| POST | `/api/user/create-user` | Register new user | No |
| POST | `/api/auth/get-token` | Login and get JWT | No |
| GET | `/api/user/{id}` | Get user profile | Optional |
| PUT | `/api/user/{id}/update` | Update profile | Yes |

### Music Catalog
| Method | Endpoint | Purpose | Auth Required |
|--------|----------|---------|---------------|
| POST | `/api/tracks/upload` | Create track metadata | Yes |
| POST | `/api/audio/stream/upload` | Upload MP3 file | Yes (upload token) |
| GET | `/api/tracks/{id}` | Get track details | No |
| POST | `/api/albums` | Create album | Yes |
| POST | `/api/images/artwork/upload` | Upload album art | Yes (upload token) |
| GET | `/api/albums/{id}` | Get album details | No |
| GET | `/api/artists/{id}` | Get artist details | No |

### Playback & Streaming
| Method | Endpoint | Purpose | Auth Required |
|--------|----------|---------|---------------|
| GET | `/api/stream/{trackId}` | Stream audio (supports Range) | No |
| POST | `/api/tracks/{id}/like` | Like track | Yes |
| POST | `/api/tracks/{id}/unlike` | Unlike track | Yes |

### Playlists
| Method | Endpoint | Purpose | Auth Required |
|--------|----------|---------|---------------|
| POST | `/api/playlists/create` | Create playlist | Yes |
| GET | `/api/playlists/{id}` | Get playlist | Yes |
| PUT | `/api/playlists/{id}/update` | Update playlist | Yes |
| POST | `/api/playlists/{id}/tracks` | Add tracks | Yes |
| DELETE | `/api/playlists/{id}/tracks/{trackId}` | Remove track | Yes |
| GET | `/api/user/music/playlists` | Get user's playlists | Yes |

### Search & Discovery
| Method | Endpoint | Purpose | Auth Required |
|--------|----------|---------|---------------|
| GET | `/api/search?query={q}&type={t}` | Search tracks/artists | No |
| GET | `/api/user/music/favorites` | Get favorites | Yes |
| GET | `/api/user/music/history` | Get listening history | Yes |

---

## Essential Code Snippets

### 1. Axios Setup with Auth
```javascript
// api/axios.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

### 2. Authentication Flow
```javascript
// Complete login flow
const login = async (email, password) => {
  // Step 1: Get user ID
  const { data: userId } = await api.post('/user/get-id', { email });
  
  // Step 2: Get JWT token
  const { data: token } = await api.post('/auth/get-token', {
    id: userId,
    password
  });
  
  // Step 3: Store token
  localStorage.setItem('auth_token', token);
  
  // Step 4: Get user profile
  const { data: user } = await api.get(`/user/${userId}`);
  
  return { user, token };
};
```

### 3. Audio Player with Range Requests
```javascript
// Simple audio player
const AudioPlayer = ({ trackId }) => {
  const audioRef = useRef(new Audio());
  
  useEffect(() => {
    audioRef.current.src = `/api/stream/${trackId}`;
    audioRef.current.load();
  }, [trackId]);
  
  const play = () => audioRef.current.play();
  const pause = () => audioRef.current.pause();
  
  return (
    <div>
      <button onClick={play}>Play</button>
      <button onClick={pause}>Pause</button>
    </div>
  );
};
```

### 4. Track Upload Flow
```javascript
// Two-step upload process
const uploadTrack = async (title, artistIds, audioFile) => {
  // Step 1: Create track metadata
  const { data } = await api.post('/tracks/upload', {
    title,
    artistIds
  });
  
  const { trackId, uploadToken } = data;
  
  // Step 2: Upload audio file
  const formData = new FormData();
  formData.append('file', audioFile);
  formData.append('id', trackId);
  
  await api.post('/audio/stream/upload', formData, {
    headers: {
      'Authorization': `Bearer ${uploadToken}`,
      'Content-Type': 'multipart/form-data'
    }
  });
  
  return trackId;
};
```

### 5. Search with Debouncing
```javascript
const useSearch = (query, delay = 300) => {
  const [results, setResults] = useState({ tracks: [], artists: [] });
  const [isLoading, setIsLoading] = useState(false);
  
  useEffect(() => {
    if (query.length < 2) {
      setResults({ tracks: [], artists: [] });
      return;
    }
    
    const timer = setTimeout(async () => {
      setIsLoading(true);
      try {
        const { data } = await api.get('/search', {
          params: { query, limit: 20 }
        });
        setResults(data);
      } finally {
        setIsLoading(false);
      }
    }, delay);
    
    return () => clearTimeout(timer);
  }, [query, delay]);
  
  return { results, isLoading };
};
```

---

## Key User Flows

### Registration → Upload → Play

```
1. REGISTER
   POST /api/user/create-user
   → Returns user UUID
   
2. LOGIN
   POST /api/user/get-id (get UUID from email)
   POST /api/auth/get-token (get JWT)
   → Store JWT token
   
3. UPLOAD TRACK
   POST /api/tracks/upload (metadata)
   → Returns trackId + uploadToken
   POST /api/audio/stream/upload (MP3 file)
   → Track becomes available
   
4. PLAY TRACK
   GET /api/stream/{trackId}
   → Stream audio with Range support
   
5. CREATE PLAYLIST
   POST /api/playlists/create
   → Returns playlistId
   POST /api/playlists/{id}/tracks (add tracks)
   
6. SEARCH
   GET /api/search?query=...
   → Returns tracks and artists
```

---

## Important Notes

### JWT Token
- **Expiration**: 7 days (604800 seconds)
- **Storage**: localStorage or sessionStorage
- **Format**: `Bearer {token}` in Authorization header
- **Claims**: Contains user ID and roles

### File Upload Constraints
**Audio Files (MP3)**:
- Format: MP3 only
- Max size: 100 MB
- Min size: 100 KB
- Requires upload token from track creation

**Images (Album Artwork)**:
- Formats: JPEG, PNG, WebP
- Max size: 10 MB
- Min size: 1 KB
- Requires upload token from album creation

### Audio Streaming
- **Protocol**: HTTP with Range requests (RFC 7233)
- **Status Codes**: 200 (full), 206 (partial)
- **Headers**: `Range: bytes=start-end`
- **Benefits**: Seeking, progressive loading, bandwidth optimization

### Search
- **Backend**: ElasticSearch
- **Features**: Full-text search, fuzzy matching, relevance scoring
- **Types**: tracks, artists, or both
- **Debouncing**: Recommended 300ms delay

---

## Common Pitfalls & Solutions

### ❌ Problem: 404 on API routes
**Solution**: Gateway strips `/api` prefix. Use `/api/tracks/...` not `/tracks/...`

### ❌ Problem: 401 Unauthorized
**Solution**: Check token is valid and not expired. Token expires after 7 days.

### ❌ Problem: Track upload fails
**Solution**: Two-step process required:
1. Create metadata first → get upload token
2. Upload file with upload token

### ❌ Problem: Audio won't seek
**Solution**: Ensure Range requests are enabled in audio element:
```javascript
audio.preload = 'metadata'; // Not 'none'
```

### ❌ Problem: CORS errors
**Solution**: Backend should allow CORS. In development, use proxy in vite.config.js:
```javascript
server: {
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

---

## Testing Checklist

### Unit Tests
- [ ] Authentication store (login, logout, token management)
- [ ] Audio player controls (play, pause, seek)
- [ ] Form validation (email, username, password)
- [ ] File upload validation (size, format)
- [ ] Search debouncing

### Integration Tests
- [ ] Complete registration flow
- [ ] Login and profile fetch
- [ ] Track upload (metadata + file)
- [ ] Playlist creation and management
- [ ] Search functionality

### E2E Tests
- [ ] User registration → login → upload → play
- [ ] Search → play track
- [ ] Create playlist → add tracks → play
- [ ] Like track → view favorites

---

## Performance Targets

| Metric | Target | Notes |
|--------|--------|-------|
| First Contentful Paint | < 1.5s | Initial page load |
| Time to Interactive | < 3.5s | Fully interactive |
| Audio Start Time | < 2s | From click to playback |
| Search Response | < 500ms | Including debounce |
| Bundle Size | < 500KB | Gzipped main bundle |

---

## Security Checklist

- [ ] JWT tokens stored securely (not in cookies without httpOnly)
- [ ] Input validation on all forms
- [ ] XSS prevention (sanitize user content)
- [ ] CSRF tokens (if backend implements)
- [ ] File upload validation (client + server)
- [ ] HTTPS in production
- [ ] Content Security Policy headers
- [ ] Rate limiting on sensitive operations

---

## Deployment Checklist

### Pre-deployment
- [ ] All tests passing
- [ ] Bundle size optimized
- [ ] Environment variables configured
- [ ] Error tracking setup (Sentry)
- [ ] Analytics configured
- [ ] Performance monitoring enabled

### Deployment
- [ ] Build production bundle
- [ ] Deploy to CDN/hosting
- [ ] Configure nginx/reverse proxy
- [ ] Set up SSL certificates
- [ ] Configure caching headers
- [ ] Enable gzip/brotli compression

### Post-deployment
- [ ] Smoke tests on production
- [ ] Monitor error rates
- [ ] Check performance metrics
- [ ] Verify analytics tracking
- [ ] Test critical user flows

---

## Resources

### Documentation
- [Part 1: Overview & API Reference](./FRONTEND-RFC.md)
- [Part 2: Implementation Details](./FRONTEND-RFC-PART2.md)
- [Part 3: Security, Testing & Deployment](./FRONTEND-RFC-PART3.md)
- [Backend Architecture](./ARCHITECTURE.md)
- [Service Documentation](./services/)

### External Resources
- [React Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)
- [Axios Documentation](https://axios-http.com)
- [Zustand Documentation](https://github.com/pmndrs/zustand)
- [HTTP Range Requests (RFC 7233)](https://tools.ietf.org/html/rfc7233)

---

## Support & Contact

For questions or issues:
1. Check the complete RFC documentation (Parts 1-3)
2. Review backend service documentation
3. Check API endpoint examples
4. Test with curl to isolate frontend vs backend issues

---

**Document Version**: 1.0  
**Last Updated**: 2026-01-11  
**Status**: Ready for Implementation

**Happy Coding! 🎵**
