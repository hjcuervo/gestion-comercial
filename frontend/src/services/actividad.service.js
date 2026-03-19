import api from './api';

const BASE = '/actividades';

export const actividadService = {
  // Tipos de actividad (catálogo)
  async listarTipos() {
    const { data } = await api.get('/catalogos/tipos-actividad');
    return data;
  },

  // Actividades
  async listarPorOportunidad(oportunidadId, params = {}) {
    const { data } = await api.get(BASE, { params: { oportunidad_id: oportunidadId, page_size: 50, ...params } });
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

  // Compromisos
  async listarCompromisosPendientes() {
    const { data } = await api.get(`${BASE}/compromisos/pendientes`);
    return data;
  },

  async listarCompromisos(actividadId) {
    const { data } = await api.get(`${BASE}/${actividadId}/compromisos`);
    return data;
  },

  async crearCompromiso(actividadId, payload) {
    const { data } = await api.post(`${BASE}/${actividadId}/compromisos`, payload);
    return data;
  },

  async actualizarCompromiso(compromisoId, payload) {
    const { data } = await api.put(`${BASE}/compromisos/${compromisoId}`, payload);
    return data;
  },
};

export default actividadService;
