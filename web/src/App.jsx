import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom';
import { useEffect } from 'react';
import { useAuthStore } from './store/authStore.js';
import { useThemeStore } from './store/themeStore.js';
import LoginForm from './components/auth/LoginForm.jsx';
import RegisterForm from './components/auth/RegisterForm.jsx';
import AudioPlayer from './components/player/AudioPlayer.jsx';
import PlayerControls from './components/player/PlayerControls.jsx';
import TrackUpload from './components/upload/TrackUpload.jsx';
import SearchBar from './components/search/SearchBar.jsx';
import PlaylistManager from './components/playlist/PlaylistManager.jsx';
import ThemeToggle from './components/theme/ThemeToggle.jsx';
import UserProfile from './components/profile/UserProfile.jsx';
import Library from './components/library/Library.jsx';

function ProtectedRoute({ children }) {
  const isAuthenticated = useAuthStore(state => state.isAuthenticated);
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}

function Home() {
  return (
    <div className="home">
      <h1>Music Streaming App</h1>
      <SearchBar />
      <Library />
    </div>
  );
}

export default function App() {
  const { isAuthenticated, logout, user, userId } = useAuthStore();
  const theme = useThemeStore(state => state.theme);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  return (
    <BrowserRouter>
      <div className="app">
        <header className="app-header">
          <nav>
            <Link to="">🎵 Home</Link>
            {isAuthenticated ? (
              <>
                <Link to="/upload">📤 Upload</Link>
                <Link to="/playlists">📋 Playlists</Link>
                <Link to={`/profile/${userId}`}>👋 Welcome, {user?.username || 'User'}</Link>
                <ThemeToggle />
                <button onClick={logout}>🚪 Logout</button>
              </>
            ) : (
              <>
                <Link to="/login">🔐 Login</Link>
                <Link to="/register">✨ Register</Link>
                <ThemeToggle />
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
            
            <Route
              path="/profile/:id"
              element={
                <ProtectedRoute>
                  <UserProfile />
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
