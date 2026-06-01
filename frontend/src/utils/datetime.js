/**
 * Utilidades de fecha para el rediseño "Instrumento".
 *
 * Se crea aparte porque utils/currency.js (real, en main) NO exporta funciones
 * de fecha — solo de moneda. Los componentes nuevos formatean fechas desde aquí.
 *
 * Acepta tanto ISO datetime ('2026-05-12T14:30:00') como date ('2026-05-12').
 * Devuelve cadena vacía si el valor es nulo o inválido.
 */

function toDate(value) {
  if (!value) return null;
  // 'YYYY-MM-DD' → construir como fecha local (evita corrimiento por zona horaria)
  if (typeof value === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(value)) {
    const [y, m, d] = value.split('-').map(Number);
    return new Date(y, m - 1, d);
  }
  const d = new Date(value);
  return isNaN(d.getTime()) ? null : d;
}

/** 2026-05-12 → "12/05/2026" */
export function formatDate(value) {
  const d = toDate(value);
  return d ? d.toLocaleDateString('es-CO') : '';
}

/** 2026-05-12T14:30 → "12/05/2026, 14:30" */
export function formatDateTime(value) {
  const d = toDate(value);
  return d
    ? d.toLocaleString('es-CO', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    : '';
}

export default { formatDate, formatDateTime };
