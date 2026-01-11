# Frontend RFC - Part 3: Security, Testing & Deployment

This is the final part of the Frontend RFC. See [FRONTEND-RFC.md](./FRONTEND-RFC.md) and [FRONTEND-RFC-PART2.md](./FRONTEND-RFC-PART2.md) for previous sections.

---

## Security Considerations

### 1. JWT Token Management

**Best Practices**:

```javascript
// Secure token storage
const TokenManager = {
  setToken: (token) => {
    // Use sessionStorage for temporary sessions
    // Use localStorage for "remember me" functionality
    sessionStorage.setItem('auth_token', token);
  },

  getToken: () => {
    return sessionStorage.getItem('auth_token');
  },

  removeToken: () => {
    sessionStorage.removeItem('auth_token');
    localStorage.removeItem('auth_token');
  },

  isTokenExpired: (token) => {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }
};
```

**Token Refresh Strategy**:
```javascript
// Check token expiration before requests
api.interceptors.request.use(async (config) => {
  const token = TokenManager.getToken();
  
  if (token && TokenManager.isTokenExpired(token)) {
    // Token expired - logout user
    useAuthStore.getState().logout();
    window.location.href = '/login';
    return Promise.reject(new Error('Token expired'));
  }
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  return config;
});
```

### 2. Input Validation & Sanitization

```javascript
// Client-side validation utilities
export const validators = {
  email: (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  },

  username: (username) => {
    // 3-50 chars, alphanumeric + underscore
    const re = /^[a-zA-Z0-9_]{3,50}$/;
    return re.test(username);
  },

  password: (password) => {
    // Minimum 8 characters
    return password.length >= 8;
  },

  sanitizeInput: (input) => {
    // Remove potentially dangerous characters
    return input
      .replace(/[<>]/g, '')
      .trim();
  }
};

// Form validation hook
const useFormValidation = (initialValues, validationRules) => {
  const [values, setValues] = useState(initialValues);
  const [errors, setErrors] = useState({});

  const validate = (fieldName, value) => {
    const rule = validationRules[fieldName];
    if (!rule) return null;

    if (rule.required && !value) {
      return 'This field is required';
    }

    if (rule.validator && !rule.validator(value)) {
      return rule.message || 'Invalid value';
    }

    return null;
  };

  const handleChange = (fieldName, value) => {
    setValues(prev => ({ ...prev, [fieldName]: value }));
    const error = validate(fieldName, value);
    setErrors(prev => ({ ...prev, [fieldName]: error }));
  };

  const validateAll = () => {
    const newErrors = {};
    Object.keys(validationRules).forEach(fieldName => {
      const error = validate(fieldName, values[fieldName]);
      if (error) newErrors[fieldName] = error;
    });
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  return { values, errors, handleChange, validateAll };
};
```

### 3. XSS Prevention

```javascript
// Sanitize user-generated content before rendering
import DOMPurify from 'dompurify';

const SafeHTML = ({ html }) => {
  const sanitized = DOMPurify.sanitize(html, {
    ALLOWED_TAGS: ['b', 'i', 'em', 'strong', 'a', 'p', 'br'],
    ALLOWED_ATTR: ['href']
  });

  return <div dangerouslySetInnerHTML={{ __html: sanitized }} />;
};

// For user bios, track descriptions, etc.
const UserBio = ({ bio }) => {
  return <SafeHTML html={bio} />;
};
```

### 4. CSRF Protection

```javascript
// CSRF token handling (if backend implements it)
api.interceptors.request.use((config) => {
  const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content;
  if (csrfToken) {
    config.headers['X-CSRF-Token'] = csrfToken;
  }
  return config;
});
```

### 5. Content Security Policy

```html
<!-- Add to index.html -->
<meta http-equiv="Content-Security-Policy" 
      content="
        default-src 'self';
        script-src 'self' 'unsafe-inline';
        style-src 'self' 'unsafe-inline';
        img-src 'self' data: http://localhost:8080;
        media-src 'self' http://localhost:8080;
        connect-src 'self' http://localhost:8080;
      ">
```

### 6. Secure File Upload

```javascript
const secureFileUpload = async (file, uploadToken) => {
  // Validate file on client side
  const errors = validateAudioFile(file);
  if (errors.length > 0) {
    throw new Error(errors.join(', '));
  }

  // Check file signature (magic bytes) for MP3
  const buffer = await file.slice(0, 3).arrayBuffer();
  const bytes = new Uint8Array(buffer);
  const isMP3 = (bytes[0] === 0xFF && bytes[1] === 0xFB) || // MP3 frame
                (bytes[0] === 0x49 && bytes[1] === 0x44 && bytes[2] === 0x33); // ID3

  if (!isMP3) {
    throw new Error('Invalid MP3 file');
  }

  // Upload with token
  const formData = new FormData();
  formData.append('file', file);
  
  return api.post('/audio/stream/upload', formData, {
    headers: {
      'Authorization': `Bearer ${uploadToken}`,
      'Content-Type': 'multipart/form-data'
    }
  });
};
```

---

## Testing Strategy

### 1. Unit Tests (Jest + React Testing Library)

```javascript
// __tests__/components/AudioPlayer.test.jsx
import { render, screen, fireEvent } from '@testing-library/react';
import AudioPlayer from '../components/player/AudioPlayer';

describe('AudioPlayer', () => {
  it('renders play button initially', () => {
    render(<AudioPlayer trackId="test-id" />);
    expect(screen.getByRole('button', { name: /play/i })).toBeInTheDocument();
  });

  it('toggles play/pause on button click', () => {
    render(<AudioPlayer trackId="test-id" />);
    const button = screen.getByRole('button', { name: /play/i });
    
    fireEvent.click(button);
    expect(screen.getByRole('button', { name: /pause/i })).toBeInTheDocument();
  });

  it('updates progress bar as track plays', async () => {
    render(<AudioPlayer trackId="test-id" />);
    // Test implementation
  });
});
```

```javascript
// __tests__/hooks/useAuth.test.js
import { renderHook, act } from '@testing-library/react-hooks';
import { useAuthStore } from '../store/authStore';

describe('useAuthStore', () => {
  beforeEach(() => {
    useAuthStore.getState().logout();
  });

  it('logs in user successfully', async () => {
    const { result } = renderHook(() => useAuthStore());
    
    await act(async () => {
      await result.current.login('test@example.com', 'password123');
    });

    expect(result.current.isAuthenticated).toBe(true);
    expect(result.current.user).toBeDefined();
    expect(result.current.token).toBeDefined();
  });

  it('logs out user', () => {
    const { result } = renderHook(() => useAuthStore());
    
    act(() => {
      result.current.logout();
    });

    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.user).toBeNull();
    expect(result.current.token).toBeNull();
  });
});
```

### 2. Integration Tests

```javascript
// __tests__/integration/TrackUpload.test.jsx
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import TrackUpload from '../pages/Upload';

const server = setupServer(
  rest.post('/api/tracks/upload', (req, res, ctx) => {
    return res(ctx.json({
      trackId: 'test-track-id',
      uploadToken: 'test-token'
    }));
  }),
  rest.post('/api/audio/stream/upload', (req, res, ctx) => {
    return res(ctx.status(200));
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('Track Upload Flow', () => {
  it('completes full upload flow', async () => {
    render(<TrackUpload />);
    
    // Fill in track metadata
    await userEvent.type(
      screen.getByLabelText(/track title/i),
      'Test Track'
    );
    
    // Submit metadata
    await userEvent.click(screen.getByRole('button', { name: /next/i }));
    
    // Upload file
    const file = new File(['test'], 'test.mp3', { type: 'audio/mpeg' });
    const input = screen.getByLabelText(/upload file/i);
    await userEvent.upload(input, file);
    
    // Wait for success
    await waitFor(() => {
      expect(screen.getByText(/upload successful/i)).toBeInTheDocument();
    });
  });
});
```

### 3. E2E Tests (Cypress)

```javascript
// cypress/e2e/user-flow.cy.js
describe('User Registration and Music Upload', () => {
  it('registers, logs in, and uploads a track', () => {
    // Register
    cy.visit('/register');
    cy.get('input[name="username"]').type('testuser');
    cy.get('input[name="email"]').type('test@example.com');
    cy.get('input[name="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    // Should be logged in and redirected
    cy.url().should('include', '/home');
    cy.contains('Welcome, testuser');

    // Navigate to upload
    cy.visit('/upload');
    
    // Fill track info
    cy.get('input[name="title"]').type('My Test Track');
    cy.get('button').contains('Next').click();

    // Upload file
    cy.get('input[type="file"]').attachFile('test-track.mp3');
    
    // Wait for upload
    cy.contains('Upload successful', { timeout: 10000 });
    
    // Verify track appears in library
    cy.visit('/library');
    cy.contains('My Test Track');
  });

  it('searches for and plays a track', () => {
    cy.visit('/');
    
    // Search
    cy.get('input[placeholder*="Search"]').type('test track');
    cy.wait(500); // Debounce
    
    // Click on result
    cy.contains('My Test Track').click();
    
    // Play track
    cy.get('button[aria-label="Play"]').click();
    
    // Verify player is active
    cy.get('.audio-player').should('be.visible');
    cy.get('button[aria-label="Pause"]').should('exist');
  });
});
```

### 4. Performance Tests

```javascript
// __tests__/performance/AudioStreaming.test.js
describe('Audio Streaming Performance', () => {
  it('loads track metadata within 1 second', async () => {
    const start = performance.now();
    
    await api.get('/tracks/test-track-id');
    
    const duration = performance.now() - start;
    expect(duration).toBeLessThan(1000);
  });

  it('starts streaming within 2 seconds', async () => {
    const audio = new Audio('/api/stream/test-track-id');
    const start = performance.now();
    
    await new Promise((resolve) => {
      audio.addEventListener('canplay', resolve);
    });
    
    const duration = performance.now() - start;
    expect(duration).toBeLessThan(2000);
  });
});
```

---

## Deployment

### 1. Environment Configuration

```javascript
// config/environment.js
const environments = {
  development: {
    API_URL: 'http://localhost:8080/api',
    WS_URL: 'ws://localhost:8080/ws',
    ENABLE_ANALYTICS: false,
    LOG_LEVEL: 'debug'
  },
  staging: {
    API_URL: 'https://staging-api.musicapp.com/api',
    WS_URL: 'wss://staging-api.musicapp.com/ws',
    ENABLE_ANALYTICS: true,
    LOG_LEVEL: 'info'
  },
  production: {
    API_URL: 'https://api.musicapp.com/api',
    WS_URL: 'wss://api.musicapp.com/ws',
    ENABLE_ANALYTICS: true,
    LOG_LEVEL: 'error'
  }
};

export const config = environments[process.env.NODE_ENV] || environments.development;
```

### 2. Build Configuration

```javascript
// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { compression } from 'vite-plugin-compression';

export default defineConfig({
  plugins: [
    react(),
    compression({
      algorithm: 'gzip',
      ext: '.gz'
    })
  ],
  build: {
    outDir: 'dist',
    sourcemap: process.env.NODE_ENV !== 'production',
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          player: ['howler'],
          ui: ['@headlessui/react', 'framer-motion']
        }
      }
    },
    chunkSizeWarningLimit: 1000
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
```

### 3. Docker Configuration

```dockerfile
# Dockerfile
FROM node:18-alpine as build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source
COPY . .

# Build
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built files
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

```nginx
# nginx.conf
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API proxy
    location /api/ {
        proxy_pass http://gateway:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # SPA fallback
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

### 4. Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  frontend:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "3000:80"
    environment:
      - NODE_ENV=production
    depends_on:
      - gateway
    networks:
      - music-app-network

networks:
  music-app-network:
    external: true
```

### 5. CI/CD Pipeline (GitHub Actions)

```yaml
# .github/workflows/deploy.yml
name: Deploy Frontend

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run linter
        run: npm run lint
      
      - name: Run tests
        run: npm test -- --coverage
      
      - name: Upload coverage
        uses: codecov/codecov-action@v3

  build:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Build
        run: npm run build
        env:
          NODE_ENV: production
      
      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: dist
          path: dist/

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Download artifacts
        uses: actions/download-artifact@v3
        with:
          name: dist
          path: dist/
      
      - name: Deploy to production
        run: |
          # Deploy to your hosting service
          # Example: AWS S3, Netlify, Vercel, etc.
          echo "Deploying to production..."
```

---

## Performance Optimization Checklist

### Build Optimization
- [ ] Code splitting by route
- [ ] Lazy loading for heavy components
- [ ] Tree shaking enabled
- [ ] Minification and compression
- [ ] Source maps only in development
- [ ] Bundle size analysis

### Runtime Optimization
- [ ] Memoization for expensive computations
- [ ] Virtual scrolling for long lists
- [ ] Image lazy loading
- [ ] Debounced search
- [ ] Throttled scroll handlers
- [ ] Web Workers for heavy processing

### Network Optimization
- [ ] HTTP/2 or HTTP/3
- [ ] CDN for static assets
- [ ] Gzip/Brotli compression
- [ ] Cache headers configured
- [ ] Service Worker for offline support
- [ ] Prefetching critical resources

### Audio Streaming Optimization
- [ ] Range requests for seeking
- [ ] Progressive loading
- [ ] Audio preloading strategy
- [ ] Buffer management
- [ ] Quality adaptation (future)

---

## Monitoring & Analytics

### 1. Error Tracking

```javascript
// Sentry integration
import * as Sentry from '@sentry/react';

Sentry.init({
  dsn: process.env.SENTRY_DSN,
  environment: process.env.NODE_ENV,
  integrations: [
    new Sentry.BrowserTracing(),
    new Sentry.Replay()
  ],
  tracesSampleRate: 0.1,
  replaysSessionSampleRate: 0.1,
  replaysOnErrorSampleRate: 1.0
});
```

### 2. Analytics

```javascript
// Google Analytics or custom analytics
const trackEvent = (category, action, label, value) => {
  if (config.ENABLE_ANALYTICS) {
    gtag('event', action, {
      event_category: category,
      event_label: label,
      value: value
    });
  }
};

// Usage
trackEvent('Music', 'play', trackId);
trackEvent('Upload', 'track_uploaded', trackId);
trackEvent('Playlist', 'created', playlistId);
```

### 3. Performance Monitoring

```javascript
// Web Vitals
import { getCLS, getFID, getFCP, getLCP, getTTFB } from 'web-vitals';

const sendToAnalytics = (metric) => {
  const body = JSON.stringify(metric);
  const url = '/api/analytics';
  
  if (navigator.sendBeacon) {
    navigator.sendBeacon(url, body);
  } else {
    fetch(url, { body, method: 'POST', keepalive: true });
  }
};

getCLS(sendToAnalytics);
getFID(sendToAnalytics);
getFCP(sendToAnalytics);
getLCP(sendToAnalytics);
getTTFB(sendToAnalytics);
```

---

## Accessibility (a11y)

### 1. Keyboard Navigation

```javascript
const AudioPlayer = () => {
  const handleKeyPress = (e) => {
    switch(e.key) {
      case ' ':
        e.preventDefault();
        togglePlay();
        break;
      case 'ArrowLeft':
        seek(currentTime - 10);
        break;
      case 'ArrowRight':
        seek(currentTime + 10);
        break;
      case 'ArrowUp':
        setVolume(Math.min(1, volume + 0.1));
        break;
      case 'ArrowDown':
        setVolume(Math.max(0, volume - 0.1));
        break;
    }
  };

  return (
    <div
      className="audio-player"
      onKeyDown={handleKeyPress}
      tabIndex={0}
      role="region"
      aria-label="Audio player"
    >
      {/* Player controls */}
    </div>
  );
};
```

### 2. ARIA Labels

```javascript
<button
  onClick={togglePlay}
  aria-label={isPlaying ? 'Pause' : 'Play'}
  aria-pressed={isPlaying}
>
  {isPlaying ? <PauseIcon /> : <PlayIcon />}
</button>

<input
  type="range"
  min="0"
  max={duration}
  value={currentTime}
  onChange={handleSeek}
  aria-label="Seek"
  aria-valuemin={0}
  aria-valuemax={duration}
  aria-valuenow={currentTime}
  aria-valuetext={`${formatTime(currentTime)} of ${formatTime(duration)}`}
/>
```

### 3. Screen Reader Support

```javascript
<div role="status" aria-live="polite" className="sr-only">
  {isPlaying ? `Playing ${trackTitle}` : 'Paused'}
</div>

<div role="alert" aria-live="assertive" className="sr-only">
  {error && error.message}
</div>
```

---

## Conclusion

This RFC provides a comprehensive guide for building a frontend application for the music streaming platform. Key takeaways:

### Core Features Implemented
✅ User authentication and registration  
✅ Track and album upload with progress tracking  
✅ Audio streaming with range requests  
✅ Search functionality with ElasticSearch  
✅ Playlist management  
✅ Listening history  
✅ Like/favorite tracks  

### Technical Highlights
- **Microservices Integration**: Seamless communication with 8 backend services
- **JWT Authentication**: Secure token-based auth with 7-day expiration
- **Range-Based Streaming**: Efficient audio playback with seeking support
- **Real-time Search**: Debounced search with ElasticSearch
- **File Upload**: Multi-step upload flow with validation
- **State Management**: Zustand for global state
- **Error Handling**: Comprehensive error boundaries and API error handling

### Next Steps
1. Implement the core components following this RFC
2. Set up testing infrastructure
3. Configure CI/CD pipeline
4. Deploy to staging environment
5. Conduct user testing
6. Optimize performance based on metrics
7. Deploy to production

### Future Enhancements
- Social features (following, sharing)
- Collaborative playlists
- Recommendation engine
- Mobile app (React Native)
- Offline mode with Service Workers
- Advanced audio features (equalizer, crossfade)
- Artist analytics dashboard
- Live streaming support

---

**Document Version**: 1.0  
**Last Updated**: 2026-01-11  
**Status**: Ready for Implementation  
**Approved By**: System Architect

For questions or clarifications, refer to the backend service documentation in [`docs/services/`](./services/).
