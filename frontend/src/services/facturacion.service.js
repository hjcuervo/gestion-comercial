import api from './api';

/**
 * Service del Mundo 3 (Flujo de Facturación).
 *
 * IMPORTANTE — Alineación con backend (19-may-2026):
 *  Este service estuvo apuntando a un namespace inexistente (/api/v1/facturacion/*)
 *  que producía 404 en todas las llamadas. Ahora se alinea con los controllers
 *  reales del repo:
 *
 *   - CompromisoIngresoController     → /api/v1/contratos/{id}/compromisos
 *                                     → /api/v1/compromisos/{id}/...
 *   - FacturaController               → /api/v1/facturas
 *   - VistaPeriodoController          → /api/v1/compromisos/vista-periodo
 *
 *  No existen aún endpoints para TRM ni Excel EMI — son gap conocido de F4
 *  (documentado en GestCom_Mundo3_Matriz_RN_Endpoints.md §3).
 *
 * Terminología del proyecto:
 *  La UI habla siempre de "Forma de Pago".
 *  El backend la llama "Compromiso de Ingreso" (`GcCompromisoIngreso`).
 *  Ambos términos refieren al mismo concepto.
 */
export const facturacionService = {

  // ============================================================
  // VISTA POR PERIODO (dashboard de facturación)
  // ============================================================

  /**
   * Carga el dashboard del mes — KPIs + 3 listas segmentadas en backend.
   *
   * @param {Object} params
   * @param {string} [params.anioMes]    "YYYY-MM"; si se omite, mes actual
   * @param {number} [params.contratoId]
   * @param {number} [params.empresaId]
   * @returns {Promise<VistaPeriodoResponse>}
   *
   * VistaPeriodoResponse {
   *   anioMes: "YYYY-MM",
   *   fechaReferencia: "YYYY-MM-DD",
   *   esMesActual: boolean,
   *   moneda: "COP",
   *   notaTrm: string|null,                       // advertencia si faltaba TRM
   *   kpis: KpisPeriodoData,
   *   arrastreMesesAnteriores: CompromisoPeriodoItem[],
   *   mesActualVencidos: CompromisoPeriodoItem[],
   *   mesActualPorVencer: CompromisoPeriodoItem[],
   * }
   *
   * KpisPeriodoData {
   *   totalPresupuestadoCOP, totalFacturadoCOP, totalSaldoPendienteCOP,
   *   porcentajeCumplimiento,
   *   conteosPorGrupo:     Map<string,int>,
   *   montosPorGrupoCOP:   Map<string,BigDecimal>,
   *   distribucionEstado:  Map<string,int>,        // 7 estados → conteos
   * }
   *
   * CompromisoPeriodoItem {
   *   id, contratoId, contratoNumero,
   *   empresaId, empresaNombre,
   *   descripcion, estado, moneda,
   *   fechaEsperadaActual,
   *   montoPresupuestado, montoFacturadoAcumulado, saldoPendiente,
   *   montoPresupuestadoCOP, montoFacturadoAcumuladoCOP, saldoPendienteCOP,
   *   trmAplicada,                                  // null si moneda=COP
   *   diasVencimiento,                              // <0 vencido, 0 hoy, >0 por vencer
   * }
   */
  async obtenerVistaPeriodo(params = {}) {
    const { data } = await api.get('/compromisos/vista-periodo', { params });
    return data;
  },

  // ============================================================
  // COMPROMISOS (Formas de Pago)
  // ============================================================

  /**
   * Lista las formas de pago de un contrato.
   * @returns {Promise<CompromisoIngresoResponse[]>}
   */
  async listarPorContrato(contratoId) {
    const { data } = await api.get(`/contratos/${contratoId}/compromisos`);
    return data;
  },

  /**
   * Crea una nueva forma de pago dentro de un contrato.
   * @param {number} contratoId
   * @param {Object} payload     // CompromisoIngresoCreateRequest
   */
  async crearCompromiso(contratoId, payload) {
    const { data } = await api.post(`/contratos/${contratoId}/compromisos`, payload);
    return data;
  },

  /**
   * Obtiene una forma de pago por id.
   *
   * CompromisoIngresoResponse {
   *   id, contratoId, contratoNumeroInterno, contratoCliente,
   *   descripcion,
   *   montoPresupuestado, montoEsperadoActual, montoFacturadoAcumulado,
   *   saldoPendiente, porcentajeCumplimiento,
   *   fechaEsperadaOriginal, fechaEsperadaActual,
   *   estado,                                  // 7 estados — ver spec v4 §2.3
   *   tipo,                                    // NUEVO | RECURRENTE
   *   enRiesgo, fechaUltimaActividad,
   *   motivoPerdida, reemplazaAId, empresaFacturacionId,
   *   moneda, ...
   * }
   */
  async obtenerCompromiso(id) {
    const { data } = await api.get(`/compromisos/${id}`);
    return data;
  },

  // ---- Transiciones de estado ----

  async iniciarGestion(id) {
    const { data } = await api.post(`/compromisos/${id}/iniciar-gestion`);
    return data;
  },

  async confirmar(id) {
    const { data } = await api.post(`/compromisos/${id}/confirmar`);
    return data;
  },

  /**
   * Reprograma la fecha esperada.
   * @param {number} id
   * @param {Object} payload     // CompromisoComandoRequest { nuevaFecha, motivo? }
   */
  async reprogramar(id, payload) {
    const { data } = await api.post(`/compromisos/${id}/reprogramar`, payload);
    return data;
  },

  /**
   * Ajusta el monto esperado actual (no toca el presupuestado, que es inmutable).
   * @param {number} id
   * @param {Object} payload     // CompromisoComandoRequest { nuevoMonto, motivo? }
   */
  async ajustarMonto(id, payload) {
    const { data } = await api.post(`/compromisos/${id}/ajustar-monto`, payload);
    return data;
  },

  /**
   * Marca la forma de pago como NO_LOGRADO. Motivo obligatorio (RN-20).
   * @param {number} id
   * @param {Object} payload     // CompromisoComandoRequest { motivo }   ← REQUERIDO
   */
  async marcarPerdido(id, payload) {
    const { data } = await api.post(`/compromisos/${id}/marcar-perdido`, payload);
    return data;
  },

  /**
   * Reactiva una forma de pago en estado final (CUMPLIDO / NO_LOGRADO).
   */
  async reactivar(id) {
    const { data } = await api.post(`/compromisos/${id}/reactivar`);
    return data;
  },

  // ---- Aplicación de facturas (N:M, ver spec v4 §3.4) ----

  /**
   * Aplica una factura a esta forma de pago.
   * Sustituye al viejo "cruzar".
   *
   * @param {number} compromisoId
   * @param {Object} payload     // RegistrarFacturaRequest { facturaId, montoAplicado }
   */
  async aplicarFactura(compromisoId, payload) {
    const { data } = await api.post(`/compromisos/${compromisoId}/facturas`, payload);
    return data;
  },

  /**
   * Revierte una aplicación de factura previa.
   * Sustituye al viejo "descruzar".
   * Requiere el id de la entrada N:M (no del compromiso), más motivo obligatorio.
   *
   * @param {number} compromisoId
   * @param {Object} payload     // RevertirFacturaRequest { compromisoFacturaId, motivo }
   */
  async revertirFactura(compromisoId, payload) {
    const { data } = await api.post(`/compromisos/${compromisoId}/facturas/revertir`, payload);
    return data;
  },

  // ---- Bitácoras ----

  /**
   * Bitácora HUMANA. Inmutable (RN-09). 10 tipos de gestión — ver spec v4 §2.4.
   * @returns {Promise<CompromisoGestionResponse[]>}
   */
  async listarGestiones(compromisoId) {
    const { data } = await api.get(`/compromisos/${compromisoId}/gestiones`);
    return data;
  },

  /**
   * Registra una gestión humana.
   * @param {Object} payload     // CompromisoGestionCreateRequest { tipoGestion, descripcion, fechaGestion }
   */
  async registrarGestion(compromisoId, payload) {
    const { data } = await api.post(`/compromisos/${compromisoId}/gestiones`, payload);
    return data;
  },

  /**
   * Bitácora SISTÉMICA (event sourcing ligero). Inmutable (RN-21). 12 tipos de evento.
   * @returns {Promise<CompromisoEventoResponse[]>}
   */
  async listarEventos(compromisoId) {
    const { data } = await api.get(`/compromisos/${compromisoId}/eventos`);
    return data;
  },

  // ============================================================
  // FACTURAS (registradas, no emitidas — FACTRO emite, GestCom solo registra)
  // ============================================================

  /**
   * Listado paginado de facturas.
   *
   * @param {Object} params
   * @param {number} [params.empresa_id]
   * @param {number} [params.page]        1-based, default 1
   * @param {number} [params.page_size]   default 20, máx 100
   * @returns {Promise<PageResponse<FacturaResponse>>}
   *
   * Acceder a paginación con `res.pagination.{page,pageSize,totalItems,totalPages}`
   * (ver utils/pagination.js, fix DT-10).
   */
  async listarFacturas(params = {}) {
    const { data } = await api.get('/facturas', { params });
    return data;
  },

  async obtenerFactura(id) {
    const { data } = await api.get(`/facturas/${id}`);
    return data;
  },

  /**
   * Registra una nueva factura emitida por FACTRO.
   * @param {Object} payload     // FacturaCreateRequest
   */
  async crearFactura(payload) {
    const { data } = await api.post('/facturas', payload);
    return data;
  },

  /**
   * Anula una factura. RN-16: bloqueada si la factura tiene aplicaciones vigentes
   * (devuelve BusinessException — hay que revertir primero las aplicaciones).
   *
   * @param {number} id
   * @param {Object} payload     // FacturaAnularRequest { motivo }
   */
  async anularFactura(id, payload) {
    const { data } = await api.post(`/facturas/${id}/anular`, payload);
    return data;
  },

  // ============================================================
  // TRM y Excel EMI — gap conocido de F4 (ver matriz RN→endpoints §3)
  // ============================================================
  // Los endpoints públicos de TRM y export EMI NO existen todavía.
  // Cuando se implementen, agregar aquí: listarTrm, crearTrm, exportarEmi, etc.
};

export default facturacionService;
