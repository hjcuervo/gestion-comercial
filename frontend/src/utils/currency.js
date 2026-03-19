/**
 * Format currency abbreviated in millions (M)
 * Examples: 6.000.000 → $6 M | 60.000.000 → $60 M | 6.000.000.000 → $6.000 M
 * Values under 1M show with thousand separators: 500.000 → $500.000
 */
export function formatCurrency(value, moneda) {
  const num = Number(value);
  if (!num) return prefijo(moneda) + '0';

  if (num >= 1000000) {
    const millions = num / 1000000;
    // Show decimals only if not integer
    const formatted = millions % 1 === 0
      ? millions.toLocaleString('es-CO')
      : millions.toLocaleString('es-CO', { minimumFractionDigits: 1, maximumFractionDigits: 1 });
    return `${prefijo(moneda)}${formatted} M`;
  }

  return prefijo(moneda) + num.toLocaleString('es-CO');
}

/**
 * Format currency full (no abbreviation) with thousand separators
 * Example: 1000000000 → $1.000.000.000
 */
export function formatCurrencyFull(value, moneda) {
  const num = Number(value);
  if (!num) return prefijo(moneda) + '0';
  return prefijo(moneda) + num.toLocaleString('es-CO');
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

function prefijo(moneda) {
  if (moneda === 'USD') return 'US$';
  if (moneda === 'EUR') return '€';
  return '$';
}
