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

  // Formas de pago — internamente "compromisos de ingreso" (renombre Mundo 3)
  // El frontend mantiene el nombre "FormaPago" en la UI; aquí solo se ajusta el endpoint.
  async listarFormasPago(contratoId) {
    const { data } = await api.get(`${BASE}/${contratoId}/compromisos`);
    return data;
  },
  async crearFormaPago(contratoId, payload) {
    const { data } = await api.post(`${BASE}/${contratoId}/compromisos`, payload);
    return data;
  },
  // Nota: el backend nuevo no expone DELETE de compromiso; la "eliminación lógica"
  // se hace vía POST /compromisos/{id}/marcar-perdido con motivo (RN-20).
  // Esta función queda como stub que arroja error claro hasta que se decida flujo.
  async eliminarFormaPago(formaPagoId) {
    throw new Error('Eliminar forma de pago no soportado. Use "marcar-perdido" vía facturacionService.');
  },

  // Modificaciones
  async listarModificaciones(contratoId) { const { data } = await api.get(`${BASE}/${contratoId}/modificaciones`); return data; },
  async crearModificacion(contratoId, payload) { const { data } = await api.post(`${BASE}/${contratoId}/modificaciones`, payload); return data; },
};

export default contratoService;
