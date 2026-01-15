import { useState, useEffect } from 'react';
import { playlistAPI } from '../../api/playlist.js';
import { usePlayerStore } from '../../store/playerStore.js';
import { useAuthStore } from '../../store/authStore.js';

export default function PlaylistManager() {
  const [playlists, setPlaylists] = useState([]);
  const [isCreating, setIsCreating] = useState(false);
  const [newPlaylistTitle, setNewPlaylistTitle] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const playQueue = usePlayerStore(state => state.playQueue);
  const userId = useAuthStore(state => state.userId);

  useEffect(() => {
    if (userId) {
      fetchPlaylists();
    }
  }, [userId]);

  const fetchPlaylists = async () => {
    try {
      const data = await playlistAPI.getUserPlaylists(userId);
      setPlaylists(data);
    } catch (err) {
      console.error('Error fetching playlists:', err);
      setError('Failed to load playlists');
    }
  };

  const handleCreatePlaylist = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await playlistAPI.createPlaylist(newPlaylistTitle, true);
      setNewPlaylistTitle('');
      setIsCreating(false);
      await fetchPlaylists();
    } catch (err) {
      setError('Failed to create playlist: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handlePlayPlaylist = async (playlistId) => {
    try {
      const playlist = await playlistAPI.getPlaylist(playlistId);
      if (playlist.tracks && playlist.tracks.length > 0) {
        playQueue(playlist.tracks, 0);
      }
    } catch (err) {
      console.error('Error loading playlist:', err);
    }
  };

  return (
    <div className="playlist-manager">
      <div className="playlist-header">
        <h1>📋 My Playlists</h1>
        {!isCreating && (
          <button onClick={() => setIsCreating(true)}>
            ➕ Create New Playlist
          </button>
        )}
      </div>

      {isCreating && (
        <div className="card" style={{ marginBottom: '2rem' }}>
          <form onSubmit={handleCreatePlaylist} className="create-playlist-form">
            <input
              type="text"
              value={newPlaylistTitle}
              onChange={(e) => setNewPlaylistTitle(e.target.value)}
              placeholder="🎵 Enter playlist name..."
              required
              disabled={loading}
              autoFocus
            />
            <button type="submit" disabled={loading}>
              {loading ? (
                <>
                  <span className="loading"></span> Creating...
                </>
              ) : (
                '✅ Create'
              )}
            </button>
            <button
              type="button"
              className="button-secondary"
              onClick={() => {
                setIsCreating(false);
                setNewPlaylistTitle('');
                setError('');
              }}
              disabled={loading}
            >
              ❌ Cancel
            </button>
          </form>
        </div>
      )}

      {error && <div className="error">❌ {error}</div>}

      {playlists.length === 0 && !loading ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🎵</div>
          <h3 style={{ marginBottom: '1rem', color: 'var(--text-primary)' }}>
            No playlists yet
          </h3>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>
            Create your first playlist to organize your favorite tracks!
          </p>
          {!isCreating && (
            <button onClick={() => setIsCreating(true)}>
              ➕ Create Your First Playlist
            </button>
          )}
        </div>
      ) : (
        <div className="playlist-grid">
          {playlists.map(playlist => (
            <div key={playlist.id} className="playlist-card">
              <div style={{
                fontSize: '3rem',
                marginBottom: '1rem',
                textAlign: 'center',
                opacity: 0.8
              }}>
                🎵
              </div>
              <h3>{playlist.title}</h3>
              <p>
                🎵 {playlist.length || 0} track{playlist.length !== 1 ? 's' : ''}
              </p>
              <p>
                ⏱️ {Math.floor((playlist.duration || 0) / 60)} min
              </p>
              <div style={{
                display: 'flex',
                gap: '0.5rem',
                marginTop: '1rem',
                flexWrap: 'wrap'
              }}>
                <button
                  onClick={() => handlePlayPlaylist(playlist.id)}
                  style={{ flex: 1 }}
                >
                  ▶️ Play
                </button>
                <a
                  href={`/playlist/${playlist.id}`}
                  className="button-secondary"
                  style={{
                    flex: 1,
                    textDecoration: 'none',
                    display: 'inline-flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}
                >
                  👁️ View
                </a>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
