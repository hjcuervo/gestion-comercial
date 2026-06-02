import { computed } from 'vue';
import { useRoute } from 'vue-router';

/**
 * Modelo de navegación de 2 niveles (reorganización en módulos de dominio).
 *
 *  Nivel 1 — MÓDULOS de dominio (barra superior): Proceso de Venta /
 *            Contratación / Facturación. Mapean 1:1 a los 3 Mundos.
 *  Nivel 2 — PANTALLAS operativas del módulo activo (barra secundaria),
 *            con su Dashboard como primera opción / aterrizaje.
 *
 * El módulo activo se deriva de `route.meta.module` (no se prefijan rutas;
 * decisión D-3 revisada para no romper deep-links ni la colección Postman).
 *
 * `landing` = a dónde se entra al pulsar el módulo en el Nivel 1.
 *   - Venta aterriza en su Dashboard real (`/`).
 *   - Contratación y Facturación aterrizan, por ahora, en su pantalla
 *     operativa principal; cuando se construyan sus Dashboards (F3 / F4)
 *     se repunta `landing` y se agrega la opción "Dashboard" a `screens`.
 */
export const MODULES = [
  {
    id: 'venta',
    label: 'Proceso de Venta',
    icon: 'target',
    landing: '/',
    screens: [
      { path: '/', label: 'Dashboard', icon: 'dashboard', exact: true },
      { path: '/actividades', label: 'Oportunidades', icon: 'target' },
      { path: '/pipeline', label: 'Pipeline', icon: 'layout-kanban' },
      { path: '/directorio', label: 'Directorio', icon: 'address-book' },
    ],
  },
  {
    id: 'contratacion',
    label: 'Contratación',
    icon: 'file-text',
    // landing → Dashboard de Contratación cuando exista (F3). Hoy: Contratos.
    landing: '/contratos',
    screens: [
      // { path: '/contratacion', label: 'Dashboard', icon: 'dashboard', exact: true }, // F3
      { path: '/contratos', label: 'Contratos', icon: 'file-text' },
    ],
  },
  {
    id: 'facturacion',
    label: 'Facturación',
    icon: 'receipt',
    // landing → Dashboard de Facturación (Panel sobre /compromisos/vista-periodo) cuando exista (F4). Hoy: Facturación.
    landing: '/facturacion',
    screens: [
      // { path: '/facturacion/dashboard', label: 'Dashboard', icon: 'dashboard', exact: true }, // F4
      { path: '/facturacion', label: 'Facturación', icon: 'receipt' },
    ],
  },
];

const DEFAULT_MODULE_ID = 'venta';

/**
 * Composable de navegación por módulo. Pensado para usarse en setup
 * (usa useRoute internamente). El singleton de estado no es necesario:
 * el módulo activo es función pura de la ruta.
 */
export function useModule() {
  const route = useRoute();

  const activeModuleId = computed(() => route.meta?.module || DEFAULT_MODULE_ID);

  const activeModule = computed(
    () => MODULES.find((m) => m.id === activeModuleId.value) || MODULES[0]
  );

  /** Pantallas (Nivel 2) del módulo activo. */
  const subnav = computed(() => activeModule.value.screens);

  return { modules: MODULES, activeModuleId, activeModule, subnav };
}

export default useModule;
