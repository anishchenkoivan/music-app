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
    }, 300); // Debounce delay

    return () => clearTimeout(timer);
  }, [query]);

  const handleTrackClick = (track) => {
    playTrack(track);
    setShowResults(false);
    setQuery('');
  };

  return (
    <div className="search-bar">
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onFocus={() => query.length >= 2 && setShowResults(true)}
        onBlur={() => setTimeout(() => setShowResults(false), 200)}
        placeholder="Search tracks, artists..."
        className="search-input"
      />
      
      {isLoading && <div className="search-loading">Searching...</div>}

      {showResults && (results.tracks.length > 0 || results.artists.length > 0) && (
        <div className="search-results">
          {results.tracks.length > 0 && (
            <div className="results-section">
              <h3>Tracks</h3>
              <ul>
                {results.tracks.map(track => (
                  <li 
                    key={track.id}
                    onClick={() => handleTrackClick(track)}
                  >
                    <span className="track-title">{track.title}</span>
                    <span className="track-artists">
                      {track.artists?.map(a => a.name).join(', ')}
                    </span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {results.artists.length > 0 && (
            <div className="results-section">
              <h3>Artists</h3>
              <ul>
                {results.artists.map(artist => (
                  <li key={artist.id}>
                    <a href={`/artist/${artist.id}`}>{artist.name}</a>
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
          <p>No results found for "{query}"</p>
        </div>
      )}
    </div>
  );
}
