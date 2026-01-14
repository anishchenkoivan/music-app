import { useState, useEffect } from 'react';
import { musicAPI } from '../../api/music.js';
import { usePlayerStore } from '../../store/playerStore.js';
import { useNavigate } from 'react-router-dom';

export default function Library() {
  const [artists, setArtists] = useState([]);
  const [albums, setAlbums] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('artists'); // 'artists' or 'albums'
  
  const { playTrack, playQueue } = usePlayerStore();
  const navigate = useNavigate();

  useEffect(() => {
    fetchLibrary();
  }, []);

  const fetchLibrary = async () => {
    try {
      setLoading(true);
      const [artistsData, albumsData] = await Promise.all([
        musicAPI.getAllArtists(),
        musicAPI.getAllAlbums()
      ]);
      setArtists(artistsData);
      setAlbums(albumsData);
    } catch (err) {
      console.error('Error fetching library:', err);
      setError('Failed to load library');
    } finally {
      setLoading(false);
    }
  };

  const handlePlayAlbum = (album) => {
    if (album.tracks && album.tracks.length > 0) {
      playQueue(album.tracks, 0);
    }
  };

  const handlePlayTrack = (track) => {
    playTrack(track);
  };

  if (loading) {
    return (
      <div className="library" style={{ textAlign: 'center', padding: '3rem' }}>
        <div className="loading" style={{ fontSize: '3rem' }}>⏳</div>
        <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>Loading library...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="library" style={{ textAlign: 'center', padding: '3rem' }}>
        <div className="error">❌ {error}</div>
        <button onClick={fetchLibrary} style={{ marginTop: '1rem' }}>
          🔄 Retry
        </button>
      </div>
    );
  }

  return (
    <div className="library">
      <h1>🎵 Music Library</h1>
      
      <div className="tabs" style={{
        display: 'flex',
        gap: '1rem',
        marginBottom: '2rem',
        borderBottom: '2px solid var(--border-color)',
        paddingBottom: '0.5rem'
      }}>
        <button
          onClick={() => setActiveTab('artists')}
          style={{
            background: activeTab === 'artists' ? 'var(--accent-gradient)' : 'transparent',
            border: 'none',
            padding: '0.75rem 1.5rem',
            borderRadius: 'var(--border-radius)',
            cursor: 'pointer',
            fontWeight: activeTab === 'artists' ? 'bold' : 'normal',
            color: activeTab === 'artists' ? 'white' : 'var(--text-primary)'
          }}
        >
          👤 Artists ({artists.length})
        </button>
        <button
          onClick={() => setActiveTab('albums')}
          style={{
            background: activeTab === 'albums' ? 'var(--accent-gradient)' : 'transparent',
            border: 'none',
            padding: '0.75rem 1.5rem',
            borderRadius: 'var(--border-radius)',
            cursor: 'pointer',
            fontWeight: activeTab === 'albums' ? 'bold' : 'normal',
            color: activeTab === 'albums' ? 'white' : 'var(--text-primary)'
          }}
        >
          💿 Albums ({albums.length})
        </button>
      </div>

      {activeTab === 'artists' && (
        <div>
          {artists.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🎤</div>
              <h3 style={{ marginBottom: '1rem', color: 'var(--text-primary)' }}>
                No artists yet
              </h3>
              <p style={{ color: 'var(--text-secondary)' }}>
                Upload some tracks to see artists here!
              </p>
            </div>
          ) : (
            <div className="artist-grid" style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
              gap: '1.5rem'
            }}>
              {artists.map(artist => (
                <div
                  key={artist.id}
                  className="card"
                  style={{
                    padding: '1.5rem',
                    textAlign: 'center',
                    cursor: 'pointer',
                    transition: 'transform 0.2s ease'
                  }}
                  onClick={() => navigate(`/artist/${artist.id}`)}
                  onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-4px)'}
                  onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
                >
                  <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>🎤</div>
                  <h3 style={{ marginBottom: '0.5rem' }}>{artist.name}</h3>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'albums' && (
        <div>
          {albums.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
              <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>💿</div>
              <h3 style={{ marginBottom: '1rem', color: 'var(--text-primary)' }}>
                No albums yet
              </h3>
              <p style={{ color: 'var(--text-secondary)' }}>
                Upload some tracks to see albums here!
              </p>
            </div>
          ) : (
            <div className="album-grid" style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
              gap: '1.5rem'
            }}>
              {albums.map(album => (
                <div key={album.id} className="card" style={{ padding: '1.5rem' }}>
                  <div style={{ fontSize: '4rem', marginBottom: '1rem', textAlign: 'center' }}>💿</div>
                  <h3 style={{ marginBottom: '0.5rem' }}>{album.title}</h3>
                  <p style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
                    👤 {album.artist?.name || 'Unknown Artist'}
                  </p>
                  <p style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
                    🎵 {album.length || 0} tracks
                  </p>
                  <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem' }}>
                    ⏱️ {Math.floor((album.duration || 0) / 60)} min
                  </p>
                  
                  {album.tracks && album.tracks.length > 0 && (
                    <div>
                      <button
                        onClick={() => handlePlayAlbum(album)}
                        style={{ width: '100%', marginBottom: '1rem' }}
                      >
                        ▶️ Play Album
                      </button>
                      
                      <div style={{
                        maxHeight: '200px',
                        overflowY: 'auto',
                        borderTop: '1px solid var(--border-color)',
                        paddingTop: '0.5rem'
                      }}>
                        {album.tracks.map((track, index) => (
                          <div
                            key={track.id}
                            style={{
                              padding: '0.5rem',
                              cursor: 'pointer',
                              borderRadius: 'var(--border-radius-sm)',
                              display: 'flex',
                              justifyContent: 'space-between',
                              alignItems: 'center'
                            }}
                            onClick={() => handlePlayTrack(track)}
                            onMouseEnter={(e) => e.currentTarget.style.background = 'var(--bg-tertiary)'}
                            onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                          >
                            <span style={{ fontSize: '0.9rem' }}>
                              {index + 1}. {track.title}
                            </span>
                            <span style={{ fontSize: '0.8rem', color: 'var(--text-tertiary)' }}>
                              {Math.floor((track.duration || 0) / 60)}:{String(Math.floor((track.duration || 0) % 60)).padStart(2, '0')}
                            </span>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
