import api from './axios.js';

export const playlistAPI = {
  createPlaylist: async (title, isPublic = true) => {
    const response = await api.post('/playlists/create', { title, isPublic });
    return response.data; // { playlistId }
  },

  getPlaylist: async (playlistId) => {
    const response = await api.get(`/playlists/${playlistId}`);
    return response.data;
  },

  updatePlaylist: async (playlistId, title, isPublic) => {
    const response = await api.put(`/playlists/${playlistId}/update`, {
      title,
      isPublic
    });
    return response.data;
  },

  addTracks: async (playlistId, trackIds) => {
    const response = await api.post(`/playlists/${playlistId}/tracks`, {
      trackIds
    });
    return response.data;
  },

  removeTrack: async (playlistId, trackId) => {
    const response = await api.delete(`/playlists/${playlistId}/tracks/${trackId}`);
    return response.data;
  },

  getUserPlaylists: async () => {
    const response = await api.get('/playlists/user');
    return response.data;
  },

  getFavorites: async () => {
    const response = await api.get('/playlists/favorites');
    return response.data;
  },

  getHistory: async (limit = 50) => {
    const response = await api.get('/playlists/history', {
      params: { limit }
    });
    return response.data;
  }
};
