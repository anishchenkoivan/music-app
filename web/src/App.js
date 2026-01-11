import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import { authService } from './api/authService';
import { musicService } from './api/musicService';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = authService.getCurrentUser();
    setIsAuthenticated(!!token);
  }, []);

  return (
    <Router>
      <div className="App">
        <nav className="navbar">
          <div className="nav-brand">
            <Link to="/">Music App</Link>
          </div>
          <div className="nav-links">
            {isAuthenticated ? (
              <>
                <Link to="/tracks">Tracks</Link>
                <Link to="/albums">Albums</Link>
                <Link to="/playlists">Playlists</Link>
                <Link to="/favorites">Favorites</Link>
                <button onClick={() => {
                  authService.logout();
                  setIsAuthenticated(false);
                  window.location.href = '/login';
                }}>Logout</button>
              </>
            ) : (
              <>
                <Link to="/login">Login</Link>
                <Link to="/register">Register</Link>
              </>
            )}
          </div>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login setIsAuthenticated={setIsAuthenticated} />} />
            <Route path="/register" element={<Register />} />
            <Route path="/tracks" element={isAuthenticated ? <Tracks /> : <Navigate to="/login" />} />
            <Route path="/albums" element={isAuthenticated ? <Albums /> : <Navigate to="/login" />} />
            <Route path="/playlists" element={isAuthenticated ? <Playlists /> : <Navigate to="/login" />} />
            <Route path="/favorites" element={isAuthenticated ? <Favorites /> : <Navigate to="/login" />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

function Home() {
  return (
    <div className="page">
      <h1>Welcome to Music App</h1>
      <p>A modern music streaming platform with microservice architecture</p>
      <div className="features">
        <div className="feature">
          <h3>🎵 Stream Music</h3>
          <p>Listen to your favorite tracks with high-quality audio streaming</p>
        </div>
        <div className="feature">
          <h3>📚 Manage Library</h3>
          <p>Create playlists, organize favorites, and track your listening history</p>
        </div>
        <div className="feature">
          <h3>🔍 Search</h3>
          <p>Find tracks, albums, and artists with powerful search capabilities</p>
        </div>
      </div>
    </div>
  );
}

function Login({ setIsAuthenticated }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await authService.login(username, password);
      setIsAuthenticated(true);
      window.location.href = '/tracks';
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    }
  };

  return (
    <div className="page">
      <div className="form-container">
        <h2>Login</h2>
        {error && <div className="error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit">Login</button>
        </form>
      </div>
    </div>
  );
}

function Register() {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    username: '',
    email: '',
    password: '',
    bio: '',
    country: '',
    profilePicture: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  // Email validation regex
  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  // Country code validation (2-letter ISO code)
  const validateCountryCode = (code) => {
    const countryRegex = /^[A-Z]{2}$/;
    return countryRegex.test(code);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error when user starts typing
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate email
    if (!validateEmail(formData.email)) {
      setError('Please enter a valid email address');
      return;
    }

    // Validate country code
    if (formData.country && !validateCountryCode(formData.country.toUpperCase())) {
      setError('Country code must be a valid 2-letter ISO code (e.g., US, RU, GB)');
      return;
    }

    try {
      // Convert country to uppercase before sending
      const registrationData = {
        ...formData,
        country: formData.country.toUpperCase()
      };
      
      await authService.register(registrationData);
      setSuccess(true);
      setTimeout(() => {
        window.location.href = '/login';
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    }
  };

  return (
    <div className="page">
      <div className="form-container">
        <h2>Register</h2>
        {error && <div className="error">{error}</div>}
        {success && <div className="success">Registration successful! Redirecting to login...</div>}
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            name="firstName"
            placeholder="First Name"
            value={formData.firstName}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="lastName"
            placeholder="Last Name"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="username"
            placeholder="Username"
            value={formData.username}
            onChange={handleChange}
            required
          />
          <input
            type="email"
            name="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
            required
          />
          <input
            type="password"
            name="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
            required
            minLength="6"
          />
          <input
            type="text"
            name="country"
            placeholder="Country Code (e.g., RU, US, GB)"
            value={formData.country}
            onChange={handleChange}
            maxLength="2"
            required
            style={{ textTransform: 'uppercase' }}
          />
          <textarea
            name="bio"
            placeholder="Bio (optional)"
            value={formData.bio}
            onChange={handleChange}
            rows="3"
          />
          <input
            type="url"
            name="profilePicture"
            placeholder="Profile Picture URL (optional)"
            value={formData.profilePicture}
            onChange={handleChange}
          />
          <button type="submit">Register</button>
        </form>
      </div>
    </div>
  );
}

function Tracks() {
  const [tracks, setTracks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTracks();
  }, []);

  const loadTracks = async () => {
    try {
      const data = await musicService.getTracks();
      setTracks(data.content || data);
    } catch (err) {
      console.error('Failed to load tracks:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="page">Loading...</div>;

  return (
    <div className="page">
      <h2>Tracks</h2>
      <div className="items-grid">
        {tracks.length === 0 ? (
          <p>No tracks available</p>
        ) : (
          tracks.map((track) => (
            <div key={track.id} className="item-card">
              <h3>{track.title}</h3>
              <p>{track.artist}</p>
              <p className="duration">{track.duration}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

function Albums() {
  const [albums, setAlbums] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAlbums();
  }, []);

  const loadAlbums = async () => {
    try {
      const data = await musicService.getAlbums();
      setAlbums(data.content || data);
    } catch (err) {
      console.error('Failed to load albums:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="page">Loading...</div>;

  return (
    <div className="page">
      <h2>Albums</h2>
      <div className="items-grid">
        {albums.length === 0 ? (
          <p>No albums available</p>
        ) : (
          albums.map((album) => (
            <div key={album.id} className="item-card">
              <h3>{album.title}</h3>
              <p>{album.artist}</p>
              <p className="year">{album.releaseYear}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

function Playlists() {
  const [playlists, setPlaylists] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadPlaylists();
  }, []);

  const loadPlaylists = async () => {
    try {
      const data = await musicService.getPlaylists();
      setPlaylists(data);
    } catch (err) {
      console.error('Failed to load playlists:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="page">Loading...</div>;

  return (
    <div className="page">
      <h2>Playlists</h2>
      <div className="items-grid">
        {playlists.length === 0 ? (
          <p>No playlists available</p>
        ) : (
          playlists.map((playlist) => (
            <div key={playlist.id} className="item-card">
              <h3>{playlist.name}</h3>
              <p>{playlist.description}</p>
              <p className="track-count">{playlist.trackCount} tracks</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

function Favorites() {
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadFavorites();
  }, []);

  const loadFavorites = async () => {
    try {
      const data = await musicService.getFavorites();
      setFavorites(data);
    } catch (err) {
      console.error('Failed to load favorites:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="page">Loading...</div>;

  return (
    <div className="page">
      <h2>Favorites</h2>
      <div className="items-grid">
        {favorites.length === 0 ? (
          <p>No favorite tracks yet</p>
        ) : (
          favorites.map((track) => (
            <div key={track.id} className="item-card">
              <h3>{track.title}</h3>
              <p>{track.artist}</p>
              <p className="duration">{track.duration}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default App;
