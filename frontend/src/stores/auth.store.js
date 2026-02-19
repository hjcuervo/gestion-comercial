import { defineStore } from 'pinia';
import { authService } from '@/services/auth.service';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: authService.getUser(),
    token: authService.getToken(),
    loading: false,
    error: null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    userName: (state) => state.user?.nombreCompleto || state.user?.username || '',
    userRole: (state) => state.user?.rol || '',
  },

  actions: {
    async login(username, password) {
      this.loading = true;
      this.error = null;
      
      try {
        const data = await authService.login(username, password);
        this.token = data.token;
        this.user = data.user;
        return true;
      } catch (error) {
        this.error = error.response?.data?.message || 'Error al iniciar sesión';
        return false;
      } finally {
        this.loading = false;
      }
    },

    async fetchCurrentUser() {
      try {
        const user = await authService.getCurrentUser();
        this.user = user;
        localStorage.setItem('user', JSON.stringify(user));
      } catch (error) {
        this.logout();
      }
    },

    logout() {
      authService.logout();
      this.user = null;
      this.token = null;
    },

    clearError() {
      this.error = null;
    }
  },
});
