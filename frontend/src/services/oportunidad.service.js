import api from './api';

const BASE = '/oportunidades';

export const oportunidadService = {
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

  async moverEtapa(id, payload) {
    const { data } = await api.post(`${BASE}/${id}/mover-etapa`, payload);
    return data;
  },

  async cerrar(id, payload) {
    const { data } = await api.post(`${BASE}/${id}/cerrar`, payload);
    return data;
  },
};

export default oportunidadService;
