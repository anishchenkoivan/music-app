import api from './axios';

export const authService = {
  login: async (username, password) => {
    // Step 1: Get user ID by username
    const userIdResponse = await api.post('/api/user/get-id', {
      username: username
    });
    const userId = userIdResponse.data;
    
    // Step 2: Get token with user ID and password
    const tokenResponse = await api.post('/api/auth/get-token', {
      id: userId,
      password: password
    });
    const token = tokenResponse.data;
    
    if (token) {
      localStorage.setItem('token', token);
    }
    return { token };
  },

  register: async (userData) => {
    // userData should contain: firstName, lastName, username, email, password, bio, country, profilePicture
    const response = await api.post('/api/user/create-user', userData);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
  },

  getCurrentUser: () => {
    return localStorage.getItem('token');
  }
};
