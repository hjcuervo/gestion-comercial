/**
 * Format currency abbreviated in millions (M)
 * Always uses currency code as prefix: COP 1.000 M, USD 10.000, EUR 50 M
 * Values under 1M show with thousand separators: COP 500.000
 */
export function formatCurrency(value, moneda) {
  const num = Number(value);
  const code = moneda || 'COP';
  if (!num) return `${code} 0`;

  if (num >= 1000000) {
    const millions = num / 1000000;
    const formatted = millions % 1 === 0
      ? millions.toLocaleString('es-CO')
      : millions.toLocaleString('es-CO', { minimumFractionDigits: 1, maximumFractionDigits: 1 });
    return `${code} ${formatted} M`;
  }

  return `${code} ${num.toLocaleString('es-CO')}`;
}

/**
 * Format currency full (no abbreviation) with thousand separators
 * Example: 1000000000 → COP 1.000.000.000
 */
export function formatCurrencyFull(value, moneda) {
  const num = Number(value);
  const code = moneda || 'COP';
  if (!num) return `${code} 0`;
  return `${code} ${num.toLocaleString('es-CO')}`;
}

/**
 * Parse a formatted money string back to number
 * "1.000.000" → 1000000
 */
export function parseMoney(str) {
  if (!str) return null;
  const cleaned = String(str).replace(/\./g, '').replace(/,/g, '');
  const num = parseInt(cleaned, 10);
  return isNaN(num) ? null : num;
}

/**
 * Format a number with thousand separators (dots)
 * 1000000 → "1.000.000"
 */
export function formatMoneyInput(value) {
  const num = Number(value);
  if (!num && num !== 0) return '';
  return num.toLocaleString('es-CO', { maximumFractionDigits: 0 });
}
