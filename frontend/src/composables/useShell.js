import { reactive } from 'vue';

/**
 * Estado de las zonas del AppShell (plantillas Operativo / Tablero / Panel).
 *
 * Singleton a nivel de módulo: hay un solo AppShell montado a la vez, así que
 * un estado compartido evita el problema de provide/inject con contenido de
 * slot (las vistas se inyectan en el árbol de App.vue, no de AppShell).
 *
 * - Plantilla Operativo  → master: true (+ aside según la pantalla)
 * - Plantilla Tablero     → master: false, aside: false (superficie full width)
 * - Plantilla Panel       → master: false, aside: false (grilla full width)
 *
 * Convención: cada vista 'app' fija las zonas que usa en onMounted y las limpia
 * en onUnmounted (setRegions / reset).
 */
const state = reactive({
  master: false,
  aside: false,
});

export function useShell() {
  function setRegions(next = {}) {
    if ('master' in next) state.master = !!next.master;
    if ('aside' in next) state.aside = !!next.aside;
  }
  function reset() {
    state.master = false;
    state.aside = false;
  }
  return { regions: state, setRegions, reset };
}

export default useShell;
