import api from './api';

const BASE = '/pipelines';

export const pipelineService = {
  // ==================== PIPELINES ====================

  /** Listar con paginación: GET /pipelines?q=&estado=&page=1&page_size=20 */
  async listar(params = {}) {
    const { data } = await api.get(BASE, { params });
    return data;
  },

  /** Listar solo activos (sin paginación): GET /pipelines/activos */
  async listarActivos() {
    const { data } = await api.get(`${BASE}/activos`);
    return data;
  },

  /** Obtener por ID (incluye etapas): GET /pipelines/:id */
  async obtenerPorId(id) {
    const { data } = await api.get(`${BASE}/${id}`);
    return data;
  },

  /** Crear: POST /pipelines  body: { nombre, descripcion } */
  async crear(payload) {
    const { data } = await api.post(BASE, payload);
    return data;
  },

  /** Actualizar: PUT /pipelines/:id  body: { nombre, descripcion, activo } */
  async actualizar(id, payload) {
    const { data } = await api.put(`${BASE}/${id}`, payload);
    return data;
  },

  // ==================== ETAPAS ====================

  /** Listar etapas: GET /pipelines/:pipelineId/etapas */
  async listarEtapas(pipelineId) {
    const { data } = await api.get(`${BASE}/${pipelineId}/etapas`);
    return data;
  },

  /** Crear etapa: POST /pipelines/:pipelineId/etapas
   *  body: { nombre, orden, probabilidadSugerida, color, modoBloqueo } */
  async crearEtapa(pipelineId, payload) {
    const { data } = await api.post(`${BASE}/${pipelineId}/etapas`, payload);
    return data;
  },

  /** Actualizar etapa: PUT /pipelines/:pipelineId/etapas/:etapaId
   *  body: { nombre, orden, probabilidadSugerida, color, modoBloqueo, estado } */
  async actualizarEtapa(pipelineId, etapaId, payload) {
    const { data } = await api.put(`${BASE}/${pipelineId}/etapas/${etapaId}`, payload);
    return data;
  },
};

export default pipelineService;
