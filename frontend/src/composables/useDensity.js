import { ref } from 'vue';

/**
 * useDensity — densidad de la interfaz (RF8.3): 'comfortable' | 'compact'.
 *
 * - Singleton; aplica data-density al <html>.
 * - El modo 'compact' reduce el padding de los renglones vía variables
 *   --gc-row-* definidas en instrument.css bajo [data-density="compact"].
 *   (Se usan CSS vars y no overrides de clase porque las vars heredan por el
 *   árbol DOM y atraviesan el <style scoped> de los componentes.)
 * - Persiste en localStorage ('gc-density'). Por defecto 'comfortable'.
 */

const STORAGE_KEY = 'gc-density';
const density = ref('comfortable');
let initialized = false;

function apply(value) {
  const root = document.documentElement;
  if (value === 'compact') root.setAttribute('data-density', 'compact');
  else root.removeAttribute('data-density');
}

function init() {
  if (initialized) return;
  initialized = true;
  let saved = null;
  try { saved = localStorage.getItem(STORAGE_KEY); } catch { /* noop */ }
  if (saved === 'compact' || saved === 'comfortable') density.value = saved;
  apply(density.value);
}

function setDensity(value) {
  density.value = value === 'compact' ? 'compact' : 'comfortable';
  apply(density.value);
  try { localStorage.setItem(STORAGE_KEY, density.value); } catch { /* noop */ }
}

function toggleDensity() {
  setDensity(density.value === 'compact' ? 'comfortable' : 'compact');
}

export function useDensity() {
  init();
  return { density, setDensity, toggleDensity };
}
