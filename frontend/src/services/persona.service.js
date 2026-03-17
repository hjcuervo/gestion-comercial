import api from './api';

const BASE = '/personas';

export const personaService = {
  async listar(params = {}) {
    const { data } = await api.get(BASE, { params });
    return data;
  },

  async obtenerPorId(id) {
    const { data } = await api.get(`${BASE}/${id}`);
    return data;
  },

  async crear(payload) {
    const { data } = await api.post(BASE, payload);
    return data;
  },

  async actualizar(id, payload) {
    const { data } = await api.put(`${BASE}/${id}`, payload);
    return data;
  },

  async asociarEmpresa(personaId, payload) {
    const { data } = await api.post(`${BASE}/${personaId}/empresas`, payload);
    return data;
  },

  async desasociarEmpresa(personaId, empresaId) {
    await api.delete(`${BASE}/${personaId}/empresas/${empresaId}`);
  },
};

export default personaService;
