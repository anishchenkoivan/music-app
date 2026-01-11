import api from './axios';

export const userService = {
  // User management
  createUser: async (userData) => {
    // userData: { username, email, password, displayName, bio }
    const response = await api.post('/api/user/create-user', userData);
    return response.data; // Returns user ID
  },

  getUser: async (userId) => {
    const response = await api.get(`/api/user/${userId}`);
    return response.data;
  },

  updateUser: async (userId, userData) => {
    // userData: { displayName, bio }
    const response = await api.put(`/api/user/${userId}/update`, userData);
    return response.data;
  },

  getUserIdByCredentials: async (username = null, email = null) => {
    const payload = {};
    if (username) payload.username = username;
    if (email) payload.email = email;
    
    const response = await api.post('/api/user/get-id', payload);
    return response.data; // Returns user ID
  },

  // Password management
  updatePassword: async (newPassword) => {
    const response = await api.put('/api/auth/update-user', {
      password: newPassword
    });
    return response.data;
  }
};
