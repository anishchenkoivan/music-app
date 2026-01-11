import api from './axios.js';

export const authAPI = {
  // Get user ID by email or username
  getUserId: async (email, username) => {
    const response = await api.post('/user/get-id', { email, username });
    return response.data;
  },

  // Get JWT token
  getToken: async (userId, password) => {
    const response = await api.post('/auth/get-token', {
      id: userId,
      password
    });
    return response.data;
  },

  // Register new user
  register: async (userData) => {
    // Ensure all required fields are present with correct format
    const requestData = {
      firstName: userData.firstName,
      lastName: userData.lastName,
      username: userData.username,
      email: userData.email,
      password: userData.password,
      country: userData.country,
      bio: userData.bio || '',
      profilePicture: userData.profilePicture || ''
    };
    
    const response = await api.post('/user/create-user', requestData);
    return response.data; // Returns user UUID
  },

  // Complete login flow
  login: async (email, password) => {
    // Step 1: Get user ID
    const userId = await authAPI.getUserId(email, null);
    
    // Step 2: Get token
    const token = await authAPI.getToken(userId, password);
    
    // Step 3: Store token
    localStorage.setItem('auth_token', token);
    localStorage.setItem('user_id', userId);
    
    return { userId, token };
  },

  // Logout
  logout: () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_id');
  }
};
