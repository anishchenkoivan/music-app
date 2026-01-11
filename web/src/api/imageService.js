import api from './axios';

export const imageService = {
  // Upload album artwork
  uploadArtwork: async (file, uploadToken) => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post('/api/images/artwork/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${uploadToken}`
      }
    });
    return response.data;
  },

  // Get artwork URL
  getArtworkUrl: (albumId) => {
    return `${api.defaults.baseURL}/api/images/artwork/${albumId}`;
  }
};
