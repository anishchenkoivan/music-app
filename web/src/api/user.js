import api from './axios.js';

export const userAPI = {
  getUser: async (userId) => {
    const response = await api.get(`/user/${userId}`);
    return response.data;
  },

  updateUser: async (userId, updates) => {
    const response = await api.put(`/user/${userId}/update`, updates);
    return response.data;
  }
};
