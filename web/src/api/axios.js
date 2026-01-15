import axios from 'axios';
import config from '../config.js';

const api = axios.create({
  baseURL: config.API_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use(
  (config) => {
    // Don't overwrite Authorization header if it's already set (e.g., for upload tokens)
    if (config.headers.Authorization) {
      return config;
    }
    
    // Try to get token from direct localStorage first (set by authAPI.login)
    let token = localStorage.getItem('auth_token');
    
    // If not found, try to get from zustand persisted storage
    if (!token) {
      try {
        const authStorage = localStorage.getItem('auth-storage');
        if (authStorage) {
          const parsed = JSON.parse(authStorage);
          token = parsed.state?.token;
        }
      } catch (e) {
        console.error('Failed to parse auth storage:', e);
      }
    }
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token');
      localStorage.removeItem('user_id');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
