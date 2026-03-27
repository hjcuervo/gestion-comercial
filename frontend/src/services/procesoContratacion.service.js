import api from './api';

const BASE = '/procesos-contratacion';

export const procesoContratacionService = {
  async iniciar(payload) {
    const { data } = await api.post(BASE, payload);
    return data;
  },

  async obtenerPorId(id) {
    const { data } = await api.get(`${BASE}/${id}`);
    return data;
  },

  async listarPorOportunidad(oportunidadId) {
    const { data } = await api.get(BASE, { params: { oportunidad_id: oportunidadId } });
    return data;
  },

  async moverEtapa(id, etapaId) {
    const { data } = await api.post(`${BASE}/${id}/mover-etapa`, { etapaId });
    return data;
  },

  async completar(id) {
    const { data } = await api.post(`${BASE}/${id}/completar`);
    return data;
  },

  async cancelar(id) {
    const { data } = await api.post(`${BASE}/${id}/cancelar`);
    return data;
  },
};

export default procesoContratacionService;
