import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom';
import { useAuthStore } from './store/authStore.js';
import LoginForm from './components/auth/LoginForm.jsx';
import RegisterForm from './components/auth/RegisterForm.jsx';
import AudioPlayer from './components/player/AudioPlayer.jsx';
import PlayerControls from './components/player/PlayerControls.jsx';
import TrackUpload from './components/upload/TrackUpload.jsx';
import SearchBar from './components/search/SearchBar.jsx';
import PlaylistManager from './components/playlist/PlaylistManager.jsx';

// Protected Route wrapper
function ProtectedRoute({ children }) {
  const isAuthenticated = useAuthStore(state => state.isAuthenticated);
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}

// Home page
function Home() {
  return (
    <div className="home">
      <h1>Music Streaming App</h1>
      <SearchBar />
      <p>Search for music or browse your library</p>
    </div>
  );
}

// Main App component
export default function App() {
  const { isAuthenticated, logout, user } = useAuthStore();

  return (
    <BrowserRouter>
      <div className="app">
        <header className="app-header">
          <nav>
            <Link to="/">Home</Link>
            {isAuthenticated ? (
              <>
                <Link to="/upload">Upload</Link>
                <Link to="/playlists">Playlists</Link>
                <span>Welcome, {user?.username || 'User'}</span>
                <button onClick={logout}>Logout</button>
              </>
            ) : (
              <>
                <Link to="/login">Login</Link>
                <Link to="/register">Register</Link>
              </>
            )}
          </nav>
        </header>

        <main className="app-main">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<LoginForm />} />
            <Route path="/register" element={<RegisterForm />} />
            
            <Route
              path="/upload"
              element={
                <ProtectedRoute>
                  <TrackUpload />
                </ProtectedRoute>
              }
            />
            
            <Route
              path="/playlists"
              element={
                <ProtectedRoute>
                  <PlaylistManager />
                </ProtectedRoute>
              }
            />
          </Routes>
        </main>

        {/* Global audio player */}
        <AudioPlayer />
        
        {/* Player controls at bottom */}
        <footer className="app-footer">
          <PlayerControls />
        </footer>
      </div>
    </BrowserRouter>
  );
}
