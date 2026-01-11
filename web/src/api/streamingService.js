import api from './axios';

export const streamingService = {
  // Upload audio file
  uploadAudio: async (file, trackId, uploadToken) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('id', trackId);

    const response = await api.post('/api/stream/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${uploadToken}`
      }
    });
    return response.data;
  },

  // Get stream URL for audio playback
  getStreamUrl: (trackId) => {
    return `${api.defaults.baseURL}/api/stream/${trackId}`;
  }
};
