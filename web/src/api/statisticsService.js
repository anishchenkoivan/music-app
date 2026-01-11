import api from './axios';

export const statisticsService = {
  // Get user's listening history
  getUserHistory: async (userId, limit = 10) => {
    const response = await api.get(`/api/history/for-user/${userId}?limit=${limit}`);
    return response.data;
  }
};
