import api from './axios.js';

export const playlistAPI = {
  createPlaylist: async (title, isPublic = true) => {
    const response = await api.post('/playlists/create', {
      title,
      isPublic,
      tracks: []
    });
    return response.data;
  },

  getPlaylist: async (playlistId) => {
    const response = await api.get(`/playlists/get/${playlistId}`);
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

  getUserPlaylists: async (userId) => {
    const response = await api.get(`/user-music/${userId}/playlists`);
    return response.data.playlists || [];
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
