import api from './axios.js';

export const searchAPI = {
  // Search tracks and artists
  search: async (query, type = null, limit = 20) => {
    const params = { query, limit };
    if (type) {
      params.type = type; // 'track' or 'artist'
    }
    const response = await api.get('/search', { params });
    return response.data; // { tracks: [], artists: [] }
  },

  // Search only tracks
  searchTracks: async (query, limit = 20) => {
    return searchAPI.search(query, 'track', limit);
  },

  // Search only artists
  searchArtists: async (query, limit = 20) => {
    return searchAPI.search(query, 'artist', limit);
  }
};
