import { ref } from 'vue';

/**
 * useTheme — tema claro/oscuro de la identidad "Instrumento" (RF8.4).
 *
 * - Singleton (estado compartido entre todos los componentes que lo usen).
 * - Aplica el atributo data-theme al <html>; los tokens --gc-* del tema oscuro
 *   viven en instrument.css bajo [data-theme="dark"].
 * - Persiste la preferencia en localStorage ('gc-theme').
 * - Si no hay preferencia guardada, respeta el prefers-color-scheme del sistema.
 *
 * Fundación CLARA por defecto (decisión del rediseño): el oscuro es opt-in.
 */

const STORAGE_KEY = 'gc-theme';
const theme = ref('light');
let initialized = false;

function apply(value) {
  const root = document.documentElement;
  if (value === 'dark') root.setAttribute('data-theme', 'dark');
  else root.removeAttribute('data-theme');
}

function init() {
  if (initialized) return;
  initialized = true;
  let saved = null;
  try { saved = localStorage.getItem(STORAGE_KEY); } catch { /* noop */ }
  if (saved === 'dark' || saved === 'light') {
    theme.value = saved;
  } else if (typeof window !== 'undefined' && window.matchMedia) {
    // Sin preferencia guardada: respetar el sistema, pero por defecto claro.
    theme.value = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }
  apply(theme.value);
}

function setTheme(value) {
  theme.value = value === 'dark' ? 'dark' : 'light';
  apply(theme.value);
  try { localStorage.setItem(STORAGE_KEY, theme.value); } catch { /* noop */ }
}

function toggleTheme() {
  setTheme(theme.value === 'dark' ? 'light' : 'dark');
}

export function useTheme() {
  init();
  return { theme, setTheme, toggleTheme };
}
