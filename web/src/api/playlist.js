import api from './axios.js';

export const playlistAPI = {
  // Create playlist
  createPlaylist: async (title, isPublic = true) => {
    const response = await api.post('/playlists/create', { title, isPublic });
    return response.data; // { playlistId }
  },

  // Get playlist details
  getPlaylist: async (playlistId) => {
    const response = await api.get(`/playlists/${playlistId}`);
    return response.data;
  },

  // Update playlist
  updatePlaylist: async (playlistId, title, isPublic) => {
    const response = await api.put(`/playlists/${playlistId}/update`, {
      title,
      isPublic
    });
    return response.data;
  },

  // Add tracks to playlist
  addTracks: async (playlistId, trackIds) => {
    const response = await api.post(`/playlists/${playlistId}/tracks`, {
      trackIds
    });
    return response.data;
  },

  // Remove track from playlist
  removeTrack: async (playlistId, trackId) => {
    const response = await api.delete(`/playlists/${playlistId}/tracks/${trackId}`);
    return response.data;
  },

  // Get user's playlists
  getUserPlaylists: async () => {
    const response = await api.get('/user/music/playlists');
    return response.data;
  },

  // Get user's favorites
  getFavorites: async () => {
    const response = await api.get('/user/music/favorites');
    return response.data;
  },

  // Get listening history
  getHistory: async (limit = 50) => {
    const response = await api.get('/user/music/history', {
      params: { limit }
    });
    return response.data;
  }
};
