import { useState, useEffect } from 'react';
import { playlistAPI } from '../../api/playlist.js';
import { usePlayerStore } from '../../store/playerStore.js';

export default function PlaylistManager() {
  const [playlists, setPlaylists] = useState([]);
  const [isCreating, setIsCreating] = useState(false);
  const [newPlaylistTitle, setNewPlaylistTitle] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const playQueue = usePlayerStore(state => state.playQueue);

  useEffect(() => {
    fetchPlaylists();
  }, []);

  const fetchPlaylists = async () => {
    try {
      const data = await playlistAPI.getUserPlaylists();
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
        <h2>My Playlists</h2>
        <button onClick={() => setIsCreating(true)}>Create New Playlist</button>
      </div>

      {isCreating && (
        <form onSubmit={handleCreatePlaylist} className="create-playlist-form">
          <input
            type="text"
            value={newPlaylistTitle}
            onChange={(e) => setNewPlaylistTitle(e.target.value)}
            placeholder="Playlist name"
            required
            disabled={loading}
          />
          <button type="submit" disabled={loading}>
            {loading ? 'Creating...' : 'Create'}
          </button>
          <button 
            type="button" 
            onClick={() => {
              setIsCreating(false);
              setNewPlaylistTitle('');
              setError('');
            }}
            disabled={loading}
          >
            Cancel
          </button>
        </form>
      )}

      {error && <div className="error">{error}</div>}

      <div className="playlist-grid">
        {playlists.map(playlist => (
          <div key={playlist.id} className="playlist-card">
            <h3>{playlist.title}</h3>
            <p>{playlist.length} tracks</p>
            <p>Duration: {Math.floor(playlist.duration / 60)} min</p>
            <button onClick={() => handlePlayPlaylist(playlist.id)}>
              Play
            </button>
            <a href={`/playlist/${playlist.id}`}>View</a>
          </div>
        ))}
      </div>

      {playlists.length === 0 && !loading && (
        <p>No playlists yet. Create one to get started!</p>
      )}
    </div>
  );
}
