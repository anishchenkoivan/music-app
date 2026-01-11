import api from './axios.js';

export const userAPI = {
  // Get user profile
  getUser: async (userId) => {
    const response = await api.get(`/user/${userId}`);
    return response.data;
  },

  // Update user profile
  updateUser: async (userId, updates) => {
    const response = await api.put(`/user/${userId}/update`, updates);
    return response.data;
  }
};
