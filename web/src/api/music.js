import api from './axios.js';

export const musicAPI = {
  // Tracks
  getTrack: async (trackId) => {
    const response = await api.get(`/tracks/${trackId}`);
    return response.data;
  },

  uploadTrackMetadata: async (title, artistIds) => {
    const response = await api.post('/tracks/upload', { title, artistIds });
    return response.data; // { trackId, uploadToken }
  },

  uploadAudioFile: async (trackId, file, uploadToken, onProgress) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('id', trackId);

    const response = await api.post('/audio/stream/upload', formData, {
      headers: {
        'Authorization': `Bearer ${uploadToken}`,
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          onProgress(percentCompleted);
        }
      }
    });
    return response.data;
  },

  likeTrack: async (trackId) => {
    const response = await api.post(`/tracks/${trackId}/like`);
    return response.data;
  },

  unlikeTrack: async (trackId) => {
    const response = await api.post(`/tracks/${trackId}/unlike`);
    return response.data;
  },

  // Albums
  getAlbum: async (albumId) => {
    const response = await api.get(`/albums/${albumId}`);
    return response.data;
  },

  createAlbum: async (artistId, title, tracks) => {
    const response = await api.post('/albums', {
      artistId,
      generalData: { title, tracks }
    });
    return response.data; // { album, uploadToken }
  },

  uploadAlbumArtwork: async (file, uploadToken, onProgress) => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post('/images/artwork/upload', formData, {
      headers: {
        'Authorization': `Bearer ${uploadToken}`,
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          onProgress(percentCompleted);
        }
      }
    });
    return response.data;
  },

  getAlbumArtwork: (albumId) => {
    return `${api.defaults.baseURL}/images/artwork/${albumId}`;
  },

  // Artists
  getArtist: async (artistId) => {
    const response = await api.get(`/artists/${artistId}`);
    return response.data;
  },

  getArtistAlbums: async (artistId) => {
    const response = await api.get(`/artists/${artistId}/albums`);
    return response.data;
  },

  getArtistTracks: async (artistId) => {
    const response = await api.get(`/artists/${artistId}/tracks`);
    return response.data;
  },

  getUserArtists: async (userId) => {
    const response = await api.get(`/artists/user/${userId}`);
    return response.data;
  },

  // Streaming
  getStreamUrl: (trackId) => {
    return `${api.defaults.baseURL}/stream/${trackId}`;
  }
};
