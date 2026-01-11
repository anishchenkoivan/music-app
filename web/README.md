# Music App Frontend

React-based frontend for the Music Streaming Application.

## Features

- User authentication (login/register)
- Browse tracks, albums, and artists
- Create and manage playlists
- Manage favorite tracks
- Search functionality
- Responsive design with dark theme

## Configuration

The frontend is configured to connect to the backend API through the `API_URL` environment variable. This can be set in the `.env` file at the project root or passed as an environment variable to the Docker container.

### Local Development

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm start
```

The app will run on `http://localhost:3000` and will connect to the backend at `http://localhost:8080` by default.

### Docker Deployment

The frontend is automatically built and deployed when using `docker-compose.deploy.yml`. The API URL is configured through the `API_URL` environment variable.

To set a custom API URL:

1. Create a `.env` file in the project root (copy from `.env.example`)
2. Set the `API_URL` variable:
   - For localhost: `API_URL=http://localhost:8080`
   - For production: `API_URL=https://your-domain.com` or `API_URL=http://your-server-ip:8080`

The frontend will be available at `http://localhost:3000` (or your configured port).

## Architecture

- **React 18** - UI framework
- **React Router** - Client-side routing
- **Axios** - HTTP client with interceptors for authentication
- **Nginx** - Production web server
- **Runtime configuration** - API URL is injected at container startup

## Project Structure

```
web/
├── public/           # Static files
├── src/
│   ├── api/         # API service layer
│   │   ├── axios.js        # Axios instance with interceptors
│   │   ├── authService.js  # Authentication API calls
│   │   └── musicService.js # Music-related API calls
│   ├── App.js       # Main application component
│   ├── App.css      # Application styles
│   ├── config.js    # Runtime configuration
│   └── index.js     # Application entry point
├── Dockerfile       # Multi-stage Docker build
├── nginx.conf       # Nginx configuration
└── env.sh          # Runtime environment injection script
```

## API Integration

The frontend communicates with the backend through the API Gateway (port 8080). All API requests include JWT authentication tokens stored in localStorage.

### Available Endpoints

- `/api/auth/*` - Authentication (login, register)
- `/api/user/*` - User management
- `/api/tracks/*` - Track operations
- `/api/albums/*` - Album operations
- `/api/artists/*` - Artist operations
- `/api/playlists/*` - Playlist management
- `/api/search` - Search functionality
- `/api/stream/*` - Audio streaming
- `/api/images/*` - Image management

## Building for Production

```bash
npm run build
```

This creates an optimized production build in the `build/` directory.
