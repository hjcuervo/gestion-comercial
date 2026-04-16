import api from './api';

const BASE = '/facturacion';

export const facturacionService = {
  // === TRM ===
  async listarTrm() {
    const { data } = await api.get(`${BASE}/trm`);
    return data;
  },
  async crearTrm(payload) {
    const { data } = await api.post(`${BASE}/trm`, payload);
    return data;
  },
  async eliminarTrm(id) {
    await api.delete(`${BASE}/trm/${id}`);
  },

  // === Emisiones Pendientes ===
  async obtenerPendientes(params = {}) {
    const { data } = await api.get(`${BASE}/emisiones-pendientes`, { params });
    return data;
  },
  async obtenerKpis() {
    const { data } = await api.get(`${BASE}/kpis`);
    return data;
  },

  // === Bitácora de Gestión ===
  async listarGestiones(formaPagoId) {
    const { data } = await api.get(`${BASE}/gestiones/${formaPagoId}`);
    return data;
  },
  async registrarGestion(formaPagoId, payload) {
    const { data } = await api.post(`${BASE}/gestiones/${formaPagoId}`, payload);
    return data;
  },

  // === Facturas ===
  async listarFacturas(params = {}) {
    const { data } = await api.get(`${BASE}/facturas`, { params });
    return data;
  },
  async obtenerFactura(id) {
    const { data } = await api.get(`${BASE}/facturas/${id}`);
    return data;
  },
  async crearFactura(payload) {
    const { data } = await api.post(`${BASE}/facturas`, payload);
    return data;
  },
  async cruzarFactura(facturaId, formaPagoId, valorFacturado = null) {
    const body = valorFacturado != null ? { valorFacturado } : {};
    await api.post(`${BASE}/facturas/${facturaId}/cruzar/${formaPagoId}`, body);
  },
  async descruzarFormaPago(formaPagoId) {
    await api.post(`${BASE}/facturas/descruzar/${formaPagoId}`);
  },
};

export default facturacionService;
