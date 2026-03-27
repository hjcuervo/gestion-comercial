import api from './api';

const BASE = '/contratos';

export const contratoService = {
  async formalizarContrato(payload) {
    const { data } = await api.post(`${BASE}/formalizar`, payload);
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

  async listarPorOportunidad(oportunidadId) {
    const { data } = await api.get(`${BASE}/oportunidad/${oportunidadId}`);
    return data;
  },

  async listarTipos() {
    const { data } = await api.get('/catalogos/tipos-contrato');
    return data;
  },

  async suspender(id) { const { data } = await api.post(`${BASE}/${id}/suspender`); return data; },
  async reiniciar(id) { const { data } = await api.post(`${BASE}/${id}/reiniciar`); return data; },
  async terminar(id) { const { data } = await api.post(`${BASE}/${id}/terminar`); return data; },
  async liquidar(id) { const { data } = await api.post(`${BASE}/${id}/liquidar`); return data; },

  // Formas de pago
  async listarFormasPago(contratoId) { const { data } = await api.get(`${BASE}/${contratoId}/formas-pago`); return data; },
  async crearFormaPago(contratoId, payload) { const { data } = await api.post(`${BASE}/${contratoId}/formas-pago`, payload); return data; },
  async eliminarFormaPago(formaPagoId) { await api.delete(`${BASE}/formas-pago/${formaPagoId}`); },

  // Modificaciones
  async listarModificaciones(contratoId) { const { data } = await api.get(`${BASE}/${contratoId}/modificaciones`); return data; },
  async crearModificacion(contratoId, payload) { const { data } = await api.post(`${BASE}/${contratoId}/modificaciones`, payload); return data; },
};

export default contratoService;
