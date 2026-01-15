import api from './axios.js';

export const authAPI = {
  getUserId: async (email, username) => {
    const response = await api.post('/user/get-id', { email, username });
    return response.data;
  },

  getToken: async (userId, password) => {
    const response = await api.post('/auth/get-token', {
      id: userId,
      password
    });
    return response.data;
  },

  register: async (userData) => {
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
    return response.data;
  },

  login: async (email, password) => {
    const userId = await authAPI.getUserId(email, null);
    const token = await authAPI.getToken(userId, password);
    localStorage.setItem('auth_token', token);
    localStorage.setItem('user_id', userId);
    
    return { userId, token };
  },

  logout: () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_id');
  }
};
