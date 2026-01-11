import api from './axios';

export const musicService = {
  // Tracks
  getTracks: async (page = 0, size = 20) => {
    const response = await api.get(`/api/tracks?page=${page}&size=${size}`);
    return response.data;
  },

  getTrack: async (id) => {
    const response = await api.get(`/api/tracks/${id}`);
    return response.data;
  },

  createTrack: async (title, artistIds, duration) => {
    const response = await api.post('/api/tracks/upload', {
      title,
      artistIds,
      duration
    });
    return response.data; // Returns { trackId, uploadToken }
  },

  // Albums
  getAlbums: async (page = 0, size = 20) => {
    const response = await api.get(`/api/albums?page=${page}&size=${size}`);
    return response.data;
  },

  getAlbum: async (id) => {
    const response = await api.get(`/api/albums/${id}`);
    return response.data;
  },

  // Artists
  getArtists: async (page = 0, size = 20) => {
    const response = await api.get(`/api/artists?page=${page}&size=${size}`);
    return response.data;
  },

  getArtist: async (id) => {
    const response = await api.get(`/api/artists/${id}`);
    return response.data;
  },

  // Playlists
  getPlaylists: async () => {
    const response = await api.get('/api/playlists');
    return response.data;
  },

  getPlaylist: async (id) => {
    const response = await api.get(`/api/playlists/${id}`);
    return response.data;
  },

  createPlaylist: async (title, isPublic = true) => {
    const response = await api.post('/api/playlists/create', { title, isPublic });
    return response.data;
  },

  addTracksToPlaylist: async (playlistId, trackIds) => {
    const response = await api.post(`/api/playlists/${playlistId}/tracks`, { trackIds });
    return response.data;
  },

  removeTrackFromPlaylist: async (playlistId, trackId) => {
    const response = await api.delete(`/api/playlists/${playlistId}/tracks/${trackId}`);
    return response.data;
  },

  // Search
  search: async (query, type = null, limit = 20) => {
    let url = `/api/search?query=${encodeURIComponent(query)}&limit=${limit}`;
    if (type) {
      url += `&type=${type}`;
    }
    const response = await api.get(url);
    return response.data;
  },

  // User library
  getFavorites: async () => {
    const response = await api.get('/api/user/music/favorites');
    return response.data;
  },

  likeTrack: async (trackId) => {
    const response = await api.post(`/api/tracks/${trackId}/like`);
    return response.data;
  },

  unlikeTrack: async (trackId) => {
    const response = await api.post(`/api/tracks/${trackId}/unlike`);
    return response.data;
  },

  getUserPlaylists: async () => {
    const response = await api.get('/api/user/music/playlists');
    return response.data;
  },

  getUserHistory: async (limit = 10) => {
    const response = await api.get(`/api/user/music/history?limit=${limit}`);
    return response.data;
  },

  // Streaming
  getStreamUrl: (trackId) => {
    return `${api.defaults.baseURL}/api/stream/${trackId}`;
  },

  // Track batch operations
  getTracksBatch: async (trackIds) => {
    const response = await api.post('/api/tracks/batch', { trackIds });
    return response.data;
  },

  // Artist operations
  createArtist: async (name, userId) => {
    const response = await api.post('/api/artists', { name, userId });
    return response.data;
  },

  getArtistAlbums: async (artistId) => {
    const response = await api.get(`/api/artists/${artistId}/albums`);
    return response.data;
  },

  getArtistTracks: async (artistId) => {
    const response = await api.get(`/api/artists/${artistId}/tracks`);
    return response.data;
  },

  getArtistByUser: async (userId) => {
    const response = await api.get(`/api/artists/user/${userId}`);
    return response.data;
  },

  // Album operations
  createAlbum: async (title, tracks) => {
    const response = await api.post('/api/albums/create', {
      title,
      tracks
    });
    return response.data; // Returns { album, uploadToken }
  },

  // Playlist operations
  updatePlaylist: async (playlistId, title, isPublic) => {
    const response = await api.put(`/api/playlists/${playlistId}/update`, { title, isPublic });
    return response.data;
  },

  deletePlaylist: async (playlistId) => {
    const response = await api.delete(`/api/playlists/${playlistId}`);
    return response.data;
  }
};
