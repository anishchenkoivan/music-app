import api from './axios.js';

export const searchAPI = {
  search: async (query, type = null, limit = 20) => {
    const params = { query, limit };
    if (type) {
      params.type = type;
    }
    const response = await api.get('/search', { params });
    return response.data; // { tracks: [], artists: [] }
  },

  searchTracks: async (query, limit = 20) => {
    return searchAPI.search(query, 'track', limit);
  },

  searchArtists: async (query, limit = 20) => {
    return searchAPI.search(query, 'artist', limit);
  }
};
