import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { authAPI } from '../api/auth.js';
import { userAPI } from '../api/user.js';

export const useAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      userId: null,
      isAuthenticated: false,

      login: async (email, password) => {
        try {
          const { userId, token } = await authAPI.login(email, password);
          
          // Get user profile
          const user = await userAPI.getUser(userId);

          set({
            user,
            token,
            userId,
            isAuthenticated: true
          });

          return true;
        } catch (error) {
          console.error('Login error:', error);
          return false;
        }
      },

      register: async (userData) => {
        try {
          const userId = await authAPI.register(userData);
          
          // Auto-login after registration
          return await get().login(userData.email, userData.password);
        } catch (error) {
          console.error('Registration error:', error);
          return false;
        }
      },

      logout: () => {
        authAPI.logout();
        set({
          user: null,
          token: null,
          userId: null,
          isAuthenticated: false
        });
      },

      updateProfile: async (updates) => {
        const { userId } = get();
        try {
          await userAPI.updateUser(userId, updates);
          
          // Refresh user data
          const user = await userAPI.getUser(userId);
          
          set({ user });
          return true;
        } catch (error) {
          console.error('Update profile error:', error);
          return false;
        }
      }
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        token: state.token,
        userId: state.userId,
        isAuthenticated: state.isAuthenticated
      })
    }
  )
);
