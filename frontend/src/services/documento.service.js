import api from './api';

const BASE = '/documentos';

export const documentoService = {
  async listarTipos() {
    const { data } = await api.get('/catalogos/tipos-documento-opp');
    return data;
  },

  async listarPorOportunidad(oportunidadId) {
    const { data } = await api.get(`${BASE}/oportunidad/${oportunidadId}`);
    return data;
  },

  async crearEnlace(payload) {
    const { data } = await api.post(`${BASE}/enlace`, payload);
    return data;
  },

  async eliminar(id) {
    await api.delete(`${BASE}/${id}`);
  },
};

export default documentoService;
