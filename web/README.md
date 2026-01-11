# Music Streaming App - Frontend

React-based frontend for the music streaming application.

## Features

- User authentication (login/register)
- Audio player with range-based streaming
- Track upload functionality
- Search (tracks and artists)
- Playlist management
- Responsive design

## Development

### Prerequisites

- Node.js 18+
- Backend services running on http://localhost:8080

### Setup

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

The app will be available at http://localhost:3000

### Build

```bash
# Build for production
npm run build

# Preview production build
npm run preview
```

## Docker

### Build

```bash
docker build -t music-app-frontend .
```

### Run

```bash
docker run -p 3000:80 music-app-frontend
```

## Project Structure

```
src/
├── api/              # API client and endpoints
│   ├── axios.js      # Axios instance with interceptors
│   ├── auth.js       # Authentication API
│   ├── music.js      # Music catalog API
│   ├── playlist.js   # Playlist API
│   ├── search.js     # Search API
│   └── user.js       # User API
├── components/       # React components
│   ├── auth/         # Authentication components
│   ├── player/       # Audio player components
│   ├── upload/       # Upload components
│   ├── search/       # Search components
│   └── playlist/     # Playlist components
├── store/            # State management (Zustand)
│   ├── authStore.js  # Authentication state
│   └── playerStore.js # Player state
├── config.js         # Configuration
├── App.jsx           # Main app component
├── main.jsx          # Entry point
└── index.css         # Global styles
```

## API Integration

The frontend communicates with the backend through the API Gateway at `/api/*`. All requests are proxied through Vite dev server or nginx in production.

### Key Endpoints

- `POST /api/user/create-user` - Register
- `POST /api/auth/get-token` - Login
- `POST /api/tracks/upload` - Upload track metadata
- `POST /api/audio/stream/upload` - Upload audio file
- `GET /api/stream/{trackId}` - Stream audio
- `GET /api/search` - Search tracks/artists
- `POST /api/playlists/create` - Create playlist

## Testing

```bash
# Run tests
npm test
```

## Environment Variables

The app uses different API URLs based on the environment:

- **Development**: `http://localhost:8080/api`
- **Production**: `/api` (proxied through nginx)

## Notes

- Audio streaming uses HTTP Range requests for seeking support
- JWT tokens are stored in localStorage
- Token expiration: 7 days
- Max file sizes: 100MB (audio), 10MB (images)
