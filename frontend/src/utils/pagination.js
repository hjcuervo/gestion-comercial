/**
 * Helper para extraer la paginación de un PageResponse<T> del backend GestCom.
 *
 * El backend devuelve siempre la forma:
 *   { data: [...], pagination: { page, pageSize, totalItems, totalPages } }
 *
 * Antes de este helper, varias vistas y stores leían `response.page` directo
 * (sin el wrapper `.pagination`), lo que dejaba `totalPages = 0` y ocultaba
 * los controles (`v-if="totalPages > 1"` siempre falso). Era el bug DT-10.
 *
 * Uso típico:
 *   const pag = extractPagination(response);
 *   // -> { page, pageSize, totalItems, totalPages }
 *
 * Defaults seguros si el backend no responde con `pagination`:
 *   { page: 1, pageSize: 20, totalItems: 0, totalPages: 0 }
 */
export function extractPagination(response) {
  const p = response?.pagination || {};
  return {
    page: p.page || 1,
    pageSize: p.pageSize || 20,
    totalItems: p.totalItems || 0,
    totalPages: p.totalPages || 0,
  };
}

export default extractPagination;
