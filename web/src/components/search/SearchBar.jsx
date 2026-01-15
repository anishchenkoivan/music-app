import { useState, useEffect } from 'react';
import { searchAPI } from '../../api/search.js';
import { usePlayerStore } from '../../store/playerStore.js';

export default function SearchBar() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState({ tracks: [], artists: [] });
  const [isLoading, setIsLoading] = useState(false);
  const [showResults, setShowResults] = useState(false);
  
  const playTrack = usePlayerStore(state => state.playTrack);

  useEffect(() => {
    if (query.length < 2) {
      setResults({ tracks: [], artists: [] });
      setShowResults(false);
      return;
    }

    const timer = setTimeout(async () => {
      setIsLoading(true);
      try {
        const data = await searchAPI.search(query);
        setResults(data);
        setShowResults(true);
      } catch (error) {
        console.error('Search error:', error);
      } finally {
        setIsLoading(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [query]);

  const handleTrackClick = (track) => {
    playTrack(track);
    setShowResults(false);
    setQuery('');
  };

  return (
    <div className="search-bar">
      <div style={{ position: 'relative' }}>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => query.length >= 2 && setShowResults(true)}
          onBlur={() => setTimeout(() => setShowResults(false), 200)}
          placeholder="🔍 Search tracks, artists, albums..."
          className="search-input"
        />
        {isLoading && (
          <div style={{
            position: 'absolute',
            right: '1rem',
            top: '50%',
            transform: 'translateY(-50%)'
          }}>
            <span className="loading"></span>
          </div>
        )}
      </div>

      {showResults && (results.tracks.length > 0 || results.artists.length > 0) && (
        <div className="search-results">
          {results.tracks.length > 0 && (
            <div className="results-section">
              <h3>🎵 Tracks</h3>
              <ul>
                {results.tracks.map(track => (
                  <li
                    key={track.id}
                    onClick={() => handleTrackClick(track)}
                  >
                    <div>
                      <span className="track-title">▶️ {track.title}</span>
                      <span className="track-artists">
                        👤 {track.artists?.map(a => a.name).join(', ') || 'Unknown Artist'}
                      </span>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {results.artists.length > 0 && (
            <div className="results-section">
              <h3>🎤 Artists</h3>
              <ul>
                {results.artists.map(artist => (
                  <li key={artist.id}>
                    <a href={`/artist/${artist.id}`}>👤 {artist.name}</a>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}

      {showResults && query.length >= 2 &&
       results.tracks.length === 0 &&
       results.artists.length === 0 &&
       !isLoading && (
        <div className="search-results">
          <div style={{
            padding: '2rem',
            textAlign: 'center',
            color: 'var(--text-secondary)'
          }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🔍</div>
            <p style={{ fontSize: '1.1rem', fontWeight: '600' }}>
              No results found for "{query}"
            </p>
            <p style={{ marginTop: '0.5rem', fontSize: '0.9rem' }}>
              Try different keywords or check your spelling
            </p>
          </div>
        </div>
      )}
    </div>
  );
}
