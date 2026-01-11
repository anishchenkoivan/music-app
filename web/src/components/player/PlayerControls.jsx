import { usePlayerStore } from '../../store/playerStore.js';

export default function PlayerControls() {
  const {
    currentTrack,
    isPlaying,
    currentTime,
    duration,
    volume,
    repeat,
    shuffle,
    togglePlay,
    playNext,
    playPrevious,
    setVolume,
    toggleRepeat,
    toggleShuffle
  } = usePlayerStore();

  const formatTime = (seconds) => {
    if (!seconds || isNaN(seconds)) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const handleSeek = (e) => {
    const audio = document.querySelector('audio');
    if (audio) {
      const rect = e.currentTarget.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const percentage = x / rect.width;
      audio.currentTime = percentage * duration;
    }
  };

  if (!currentTrack) {
    return (
      <div className="player-controls empty">
        <div style={{ textAlign: 'center', opacity: 0.7 }}>
          <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>🎵</div>
          <p>No track playing - Start exploring music!</p>
        </div>
      </div>
    );
  }

  return (
    <div className="player-controls">
      <div className="track-info">
        <div>
          <h3>🎵 {currentTrack.title}</h3>
          <p>👤 {currentTrack.artists?.map(a => a.name).join(', ') || 'Unknown Artist'}</p>
        </div>
      </div>

      <div className="controls">
        <button onClick={playPrevious} title="Previous" aria-label="Previous track">
          ⏮️
        </button>
        <button
          onClick={togglePlay}
          title={isPlaying ? 'Pause' : 'Play'}
          className="play-pause"
          aria-label={isPlaying ? 'Pause' : 'Play'}
        >
          {isPlaying ? '⏸️' : '▶️'}
        </button>
        <button onClick={playNext} title="Next" aria-label="Next track">
          ⏭️
        </button>
        <button
          onClick={toggleRepeat}
          title={`Repeat: ${repeat}`}
          className={repeat !== 'off' ? 'active' : ''}
          aria-label={`Repeat mode: ${repeat}`}
        >
          🔁
        </button>
        <button
          onClick={toggleShuffle}
          title={shuffle ? 'Shuffle: On' : 'Shuffle: Off'}
          className={shuffle ? 'active' : ''}
          aria-label={`Shuffle ${shuffle ? 'on' : 'off'}`}
        >
          🔀
        </button>
      </div>

      <div className="progress-section">
        <span className="time">{formatTime(currentTime)}</span>
        <div className="progress-bar" onClick={handleSeek} role="slider" aria-label="Seek">
          <div
            className="progress-fill"
            style={{ width: `${(currentTime / duration) * 100 || 0}%` }}
          />
        </div>
        <span className="time">{formatTime(duration)}</span>
      </div>

      <div className="volume-control">
        <button
          onClick={() => setVolume(volume > 0 ? 0 : 1)}
          title={volume > 0 ? 'Mute' : 'Unmute'}
          aria-label={volume > 0 ? 'Mute' : 'Unmute'}
        >
          {volume === 0 ? '🔇' : volume < 0.5 ? '🔉' : '🔊'}
        </button>
        <input
          type="range"
          min="0"
          max="1"
          step="0.01"
          value={volume}
          onChange={(e) => setVolume(parseFloat(e.target.value))}
          aria-label="Volume"
          title={`Volume: ${Math.round(volume * 100)}%`}
        />
      </div>
    </div>
  );
}
