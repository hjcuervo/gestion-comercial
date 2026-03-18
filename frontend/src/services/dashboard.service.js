import api from './api';

export const dashboardService = {
  async obtenerStats(params = {}) {
    const { data } = await api.get('/oportunidades/stats', { params });
    return data;
  },
};

export default dashboardService;
