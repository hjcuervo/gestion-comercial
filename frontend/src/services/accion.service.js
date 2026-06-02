import api from './api';

const BASE = '/acciones';
const BASE_DISPOSICIONES = '/disposiciones';

export const accionService = {
  /** Cola "Hoy" del usuario actual. Respuesta paginada: { data, pagination }. */
  async obtenerCola(topeVisible = 50) {
    const { data } = await api.get(`${BASE}/cola`, { params: { tope_visible: topeVisible } });
    return data;
  },

  async tomar(id) {
    const { data } = await api.post(`${BASE}/${id}/tomar`);
    return data;
  },

  async completar(id, disposicionId) {
    const { data } = await api.post(`${BASE}/${id}/completar`, { disposicionId });
    return data;
  },

  async posponer(id, motivo, nuevaFecha = null) {
    const { data } = await api.post(`${BASE}/${id}/posponer`, { motivo, nuevaFecha });
    return data;
  },

  async omitir(id, motivo) {
    const { data } = await api.post(`${BASE}/${id}/omitir`, { motivo });
    return data;
  },

  async reasignar(id, nuevoResponsableId) {
    const { data } = await api.post(`${BASE}/${id}/reasignar`, { nuevoResponsableId });
    return data;
  },

  async historial(id) {
    const { data } = await api.get(`${BASE}/${id}/historial`);
    return data;
  },

  /** Disposiciones del catálogo. Si se pasa `origen`, solo las aplicables a ese origen. */
  async listarDisposiciones(origen = null) {
    const { data } = await api.get(BASE_DISPOSICIONES, { params: origen ? { origen } : {} });
    return data;
  },
};

export default accionService;
