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
        <p>No track playing</p>
      </div>
    );
  }

  return (
    <div className="player-controls">
      <div className="track-info">
        <h3>{currentTrack.title}</h3>
        <p>{currentTrack.artists?.map(a => a.name).join(', ')}</p>
      </div>

      <div className="controls">
        <button onClick={playPrevious} title="Previous">⏮</button>
        <button onClick={togglePlay} title={isPlaying ? 'Pause' : 'Play'}>
          {isPlaying ? '⏸' : '▶'}
        </button>
        <button onClick={playNext} title="Next">⏭</button>
        <button 
          onClick={toggleRepeat} 
          title={`Repeat: ${repeat}`}
          className={repeat !== 'off' ? 'active' : ''}
        >
          🔁
        </button>
        <button 
          onClick={toggleShuffle} 
          title="Shuffle"
          className={shuffle ? 'active' : ''}
        >
          🔀
        </button>
      </div>

      <div className="progress-section">
        <span className="time">{formatTime(currentTime)}</span>
        <div className="progress-bar" onClick={handleSeek}>
          <div 
            className="progress-fill" 
            style={{ width: `${(currentTime / duration) * 100}%` }}
          />
        </div>
        <span className="time">{formatTime(duration)}</span>
      </div>

      <div className="volume-control">
        <span>🔊</span>
        <input
          type="range"
          min="0"
          max="1"
          step="0.01"
          value={volume}
          onChange={(e) => setVolume(parseFloat(e.target.value))}
        />
      </div>
    </div>
  );
}
