import api from './api';

/**
 * Service de contactabilidad (Mundo 1 — F-CT3).
 * Sin try/catch: los errores burbujean a la vista (convención del repo).
 *
 * Backend:
 *  - GET/POST  /empresas/{empresaId}/contactos
 *  - GET/POST  /personas/{personaId}/contactos
 *  - PUT/DELETE /contactos/{id}
 *  - POST      /contactos/{id}/principal
 *  - GET       /contactos/{id}/llamada | /correo
 *  - GET       /redes-sociales
 */
export const contactoService = {
  // ==================== LISTADOS ====================
  async listarPorEmpresa(empresaId, params = {}) {
    const { data } = await api.get(`/empresas/${empresaId}/contactos`, { params });
    return data;
  },

  async listarPorPersona(personaId, params = {}) {
    const { data } = await api.get(`/personas/${personaId}/contactos`, { params });
    return data;
  },

  // ==================== ALTA ====================
  async crearParaEmpresa(empresaId, payload) {
    const { data } = await api.post(`/empresas/${empresaId}/contactos`, payload);
    return data;
  },

  async crearParaPersona(personaId, payload) {
    const { data } = await api.post(`/personas/${personaId}/contactos`, payload);
    return data;
  },

  // ==================== EDICIÓN / BAJA ====================
  async actualizar(id, payload) {
    const { data } = await api.put(`/contactos/${id}`, payload);
    return data;
  },

  async eliminar(id) {
    await api.delete(`/contactos/${id}`);
  },

  async marcarPrincipal(id) {
    const { data } = await api.post(`/contactos/${id}/principal`);
    return data;
  },

  // ==================== ACCIONES EXTERNAS ====================
  async payloadLlamada(id) {
    const { data } = await api.get(`/contactos/${id}/llamada`);
    return data;
  },

  async payloadCorreo(id) {
    const { data } = await api.get(`/contactos/${id}/correo`);
    return data;
  },

  // ==================== CATÁLOGO ====================
  async listarRedesSociales() {
    const { data } = await api.get('/redes-sociales');
    return data;
  },
};

export default contactoService;
