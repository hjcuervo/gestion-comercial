import api from './api';

const BASE = '/empresas';
const CATALOGOS = '/catalogos';

export const empresaService = {
  // ==================== EMPRESAS ====================
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

  // ==================== CATÁLOGOS ====================
  async listarTiposDocumento() {
    const { data } = await api.get(`${CATALOGOS}/tipos-documento`);
    return data;
  },

  async listarPaises() {
    const { data } = await api.get(`${CATALOGOS}/paises`);
    return data;
  },

  async listarDepartamentos() {
    const { data } = await api.get(`${CATALOGOS}/departamentos`);
    return data;
  },

  async listarMunicipios(departamento) {
    const params = departamento ? { departamento } : {};
    const { data } = await api.get(`${CATALOGOS}/municipios`, { params });
    return data;
  },
};

export default empresaService;
