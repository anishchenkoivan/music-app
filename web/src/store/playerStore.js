import { create } from 'zustand';

export const usePlayerStore = create((set, get) => ({
  currentTrack: null,
  queue: [],
  isPlaying: false,
  currentTime: 0,
  duration: 0,
  volume: 1,
  repeat: 'off', // 'off', 'one', 'all'
  shuffle: false,
  audioRef: null,

  playTrack: (track) => {
    set({
      currentTrack: track,
      isPlaying: true,
      currentTime: 0
    });
  },

  playQueue: (tracks, startIndex = 0) => {
    set({
      queue: tracks,
      currentTrack: tracks[startIndex],
      isPlaying: true,
      currentTime: 0
    });
  },

  playNext: () => {
    const { queue, currentTrack, repeat, shuffle } = get();
    const currentIndex = queue.findIndex(t => t.id === currentTrack?.id);
    
    if (repeat === 'one') {
      set({ currentTime: 0 });
      return;
    }

    let nextIndex;
    if (shuffle) {
      nextIndex = Math.floor(Math.random() * queue.length);
    } else {
      nextIndex = currentIndex + 1;
    }

    if (nextIndex >= queue.length) {
      if (repeat === 'all') {
        nextIndex = 0;
      } else {
        set({ isPlaying: false });
        return;
      }
    }

    set({
      currentTrack: queue[nextIndex],
      currentTime: 0
    });
  },

  playPrevious: () => {
    const { queue, currentTrack } = get();
    const currentIndex = queue.findIndex(t => t.id === currentTrack?.id);
    const prevIndex = currentIndex - 1;

    if (prevIndex >= 0) {
      set({
        currentTrack: queue[prevIndex],
        currentTime: 0
      });
    }
  },

  togglePlay: () => {
    set(state => ({ isPlaying: !state.isPlaying }));
  },

  setVolume: (volume) => {
    set({ volume: Math.max(0, Math.min(1, volume)) });
  },

  setCurrentTime: (time) => {
    set({ currentTime: time });
  },

  setDuration: (duration) => {
    set({ duration });
  },

  setAudioRef: (ref) => {
    set({ audioRef: ref });
  },

  seek: (time) => {
    const { audioRef } = get();
    if (audioRef && audioRef.current) {
      audioRef.current.currentTime = time;
      set({ currentTime: time });
    }
  },

  toggleRepeat: () => {
    const modes = ['off', 'all', 'one'];
    const { repeat } = get();
    const currentIndex = modes.indexOf(repeat);
    const nextIndex = (currentIndex + 1) % modes.length;
    set({ repeat: modes[nextIndex] });
  },

  toggleShuffle: () => {
    set(state => ({ shuffle: !state.shuffle }));
  },

  addToQueue: (track) => {
    set(state => ({
      queue: [...state.queue, track]
    }));
  },

  clearQueue: () => {
    set({ queue: [], currentTrack: null, isPlaying: false });
  }
}));
