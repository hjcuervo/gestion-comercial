import api from './api';

const BASE = '/pipelines';

export const pipelineService = {
  async listarActivos(ambito) {
    const params = {};
    if (ambito) params.ambito = ambito;
    const { data } = await api.get(`${BASE}/activos`, { params });
    return data;
  },

  async obtenerPorId(id) {
    const { data } = await api.get(`${BASE}/${id}`);
    return data;
  },

  async listar(params = {}) {
    const { data } = await api.get(BASE, { params });
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

  async listarEtapas(pipelineId, params = {}) {
    const { data } = await api.get(`${BASE}/${pipelineId}/etapas`, { params });
    return data;
  },

  async crearEtapa(pipelineId, payload) {
    const { data } = await api.post(`${BASE}/${pipelineId}/etapas`, payload);
    return data;
  },

  async actualizarEtapa(pipelineId, etapaId, payload) {
    const { data } = await api.put(`${BASE}/${pipelineId}/etapas/${etapaId}`, payload);
    return data;
  },
};

export default pipelineService;
