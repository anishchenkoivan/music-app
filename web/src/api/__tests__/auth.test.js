import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authAPI } from '../auth.js';
import api from '../axios.js';

// Mock axios
vi.mock('../axios.js', () => ({
  default: {
    post: vi.fn()
  }
}));

describe('authAPI', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  describe('getUserId', () => {
    it('should get user ID by email', async () => {
      const mockUserId = 'user-123';
      api.post.mockResolvedValue({ data: mockUserId });

      const result = await authAPI.getUserId('test@example.com', null);

      expect(api.post).toHaveBeenCalledWith('/user/get-id', {
        email: 'test@example.com',
        username: null
      });
      expect(result).toBe(mockUserId);
    });
  });

  describe('getToken', () => {
    it('should get JWT token', async () => {
      const mockToken = 'jwt-token-123';
      api.post.mockResolvedValue({ data: mockToken });

      const result = await authAPI.getToken('user-123', 'password');

      expect(api.post).toHaveBeenCalledWith('/auth/get-token', {
        id: 'user-123',
        password: 'password'
      });
      expect(result).toBe(mockToken);
    });
  });

  describe('register', () => {
    it('should register new user', async () => {
      const mockUserId = 'user-123';
      const userData = {
        username: 'testuser',
        email: 'test@example.com',
        password: 'password123'
      };
      
      api.post.mockResolvedValue({ data: mockUserId });

      const result = await authAPI.register(userData);

      expect(api.post).toHaveBeenCalledWith('/user/create-user', userData);
      expect(result).toBe(mockUserId);
    });
  });

  describe('login', () => {
    it('should complete login flow', async () => {
      const mockUserId = 'user-123';
      const mockToken = 'jwt-token-123';
      
      api.post
        .mockResolvedValueOnce({ data: mockUserId })  // getUserId
        .mockResolvedValueOnce({ data: mockToken });  // getToken

      const result = await authAPI.login('test@example.com', 'password');

      expect(result).toEqual({ userId: mockUserId, token: mockToken });
      expect(localStorage.getItem('auth_token')).toBe(mockToken);
      expect(localStorage.getItem('user_id')).toBe(mockUserId);
    });
  });

  describe('logout', () => {
    it('should clear stored credentials', () => {
      localStorage.setItem('auth_token', 'token');
      localStorage.setItem('user_id', 'user-123');

      authAPI.logout();

      expect(localStorage.getItem('auth_token')).toBeNull();
      expect(localStorage.getItem('user_id')).toBeNull();
    });
  });
});
