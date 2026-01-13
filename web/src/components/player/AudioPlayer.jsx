import { useEffect, useRef, useState } from 'react';
import { usePlayerStore } from '../../store/playerStore.js';
import { musicAPI } from '../../api/music.js';

export default function AudioPlayer() {
  const audioRef = useRef(null);
  const [error, setError] = useState(null);
  
  const {
    currentTrack,
    isPlaying,
    volume,
    setCurrentTime,
    setDuration,
    playNext
  } = usePlayerStore();

  useEffect(() => {
    if (!audioRef.current) {
      audioRef.current = new Audio();
      audioRef.current.preload = 'metadata';
    }

    const audio = audioRef.current;

    const handleLoadedMetadata = () => {
      setDuration(audio.duration);
      setError(null);
    };

    const handleTimeUpdate = () => {
      setCurrentTime(audio.currentTime);
    };

    const handleEnded = () => {
      playNext();
    };

    const handleError = (e) => {
      console.error('Audio error:', e);
      setError('Failed to load audio');
    };

    audio.addEventListener('loadedmetadata', handleLoadedMetadata);
    audio.addEventListener('timeupdate', handleTimeUpdate);
    audio.addEventListener('ended', handleEnded);
    audio.addEventListener('error', handleError);

    return () => {
      audio.removeEventListener('loadedmetadata', handleLoadedMetadata);
      audio.removeEventListener('timeupdate', handleTimeUpdate);
      audio.removeEventListener('ended', handleEnded);
      audio.removeEventListener('error', handleError);
    };
  }, [setCurrentTime, setDuration, playNext]);

  useEffect(() => {
    if (currentTrack && audioRef.current) {
      const audio = audioRef.current;
      audio.src = musicAPI.getStreamUrl(currentTrack.id);
      audio.load();
      
      if (isPlaying) {
        audio.play().catch(err => {
          console.error('Play error:', err);
          setError('Failed to play audio');
        });
      }
    }
  }, [currentTrack]);

  useEffect(() => {
    if (audioRef.current) {
      if (isPlaying) {
        audioRef.current.play().catch(err => {
          console.error('Play error:', err);
        });
      } else {
        audioRef.current.pause();
      }
    }
  }, [isPlaying]);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.volume = volume;
    }
  }, [volume]);

  if (!currentTrack) {
    return null;
  }

  return (
    <div className="audio-player">
      {error && <div className="player-error">{error}</div>}
      {/* Audio element is controlled programmatically */}
    </div>
  );
}
