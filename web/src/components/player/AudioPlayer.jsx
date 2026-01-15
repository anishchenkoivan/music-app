import { useEffect, useRef, useState } from 'react';
import { usePlayerStore } from '../../store/playerStore.js';
import { musicAPI } from '../../api/music.js';
import api from '../../api/axios.js';

export default function AudioPlayer() {
  const audioRef = useRef(null);
  const [error, setError] = useState(null);
  
  const {
    currentTrack,
    isPlaying,
    volume,
    setCurrentTime,
    setDuration,
    setAudioRef,
    playNext
  } = usePlayerStore();

  useEffect(() => {
    if (!audioRef.current) {
      audioRef.current = new Audio();
      audioRef.current.preload = 'metadata';
      setAudioRef(audioRef);
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
      
      // Fetch the authenticated stream URL
      const getStreamUrl = async () => {
        try {
          // Use dataId for streaming, not the track view id
          const trackDataId = currentTrack.dataId || currentTrack.id;
          console.log('Loading track:', currentTrack.title);
          console.log('Using trackDataId:', trackDataId);
          
          const streamUrl = `/stream/${trackDataId}`;
          
          // Backend now returns JSON with the streaming URL instead of redirecting
          const response = await api.get(streamUrl);
          
          if (response.data && response.data.url) {
            // The backend returns the gateway path like /audio/{id}?token={token}
            // We need to construct the full URL
            const streamingUrl = response.data.url.startsWith('http') 
              ? response.data.url 
              : `${api.defaults.baseURL}${response.data.url}`;
            
            console.log('Setting audio.src to:', streamingUrl);
            audio.src = streamingUrl;
            audio.load();
            
            // Auto-play if isPlaying is true when track loads
            if (isPlaying) {
              audio.play().catch(playErr => {
                console.error('Play error:', playErr);
                setError('Failed to play audio');
              });
            }
          } else {
            console.error('Invalid response from stream endpoint:', response.data);
            setError('Invalid streaming URL');
          }
        } catch (err) {
          console.error('Error fetching stream URL:', err);
          setError('Failed to load stream');
        }
      };
      
      getStreamUrl();
    }
  }, [currentTrack]); // Remove isPlaying from dependencies

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
