import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';

/**
 * meta.layout:
 *  - 'blank' → sin chasis (Login).
 *  - 'app'   → shell (AppShell). Todas las pantallas migradas al rediseño.
 *
 * meta.module:  módulo de dominio al que pertenece la ruta. Controla el
 *   resaltado del Nivel 1 (barra superior) y la barra secundaria (Nivel 2).
 *   Valores: 'venta' | 'contratacion' | 'facturacion'. Ver composables/useModule.js.
 */
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false, layout: 'blank' },
  },

  // ====================================================================
  // MÓDULO: Proceso de Venta (Mundo 1 — Comercial)
  // ====================================================================

  // Dashboard comercial (plantilla Panel). Aterrizaje del módulo Venta.
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true, layout: 'app', module: 'venta' },
  },

  // Bandeja de trabajo (Motor de Acción): cola operativa de la persona.
  // Plantilla Operativo (lista maestra de acciones + ejecución en la superficie).
  {
    path: '/bandeja',
    name: 'Bandeja',
    component: () => import('@/views/BandejaView.vue'),
    meta: { requiresAuth: true, layout: 'app', module: 'venta' },
  },

  // Consola Operativo: Oportunidades/Actividades. Lista maestra persistente;
  // el detalle (pestañas Resumen/Actividades/Proceso) se pinta en la superficie.
  {
    path: '/actividades',
    name: 'Actividades',
    component: () => import('@/views/ActividadesView.vue'),
    meta: { requiresAuth: true, layout: 'app', module: 'venta' },
    children: [
      {
        path: ':oportunidadId',
        name: 'ActividadOportunidad',
        component: () => import('@/views/OportunidadDetalleView.vue'),
        meta: { requiresAuth: true, layout: 'app', module: 'venta' },
      },
    ],
  },

  // Pipeline (plantilla Tablero).
  {
    path: '/pipeline',
    name: 'Pipeline',
    component: () => import('@/views/PipelineView.vue'),
    meta: { requiresAuth: true, layout: 'app', module: 'venta' },
  },

  // Detalle de oportunidad directo (enlace desde el Kanban). Reutiliza la vista de detalle.
  {
    path: '/oportunidades/:id',
    name: 'OportunidadDetalle',
    component: () => import('@/views/OportunidadDetalleView.vue'),
    meta: { requiresAuth: true, layout: 'app', module: 'venta' },
  },

  // Directorio unificado: Empresas + Personas en un solo módulo (enfoque A).
  // :modo = empresa | persona ; :id opcional (lista sin selección si falta).
  {
    path: '/directorio/:modo(empresa|persona)/:id?',
    name: 'Directorio',
    component: () => import('@/views/DirectorioView.vue'),
    meta: { requiresAuth: true, layout: 'app', module: 'venta' },
  },
  { path: '/directorio', redirect: { name: 'Directorio', params: { modo: 'empresa' } } },

  // Compatibilidad: las rutas viejas redirigen al Directorio (no rompe deep-links).
  {
    path: '/empresas/:id?',
    redirect: (to) => ({ name: 'Directorio', params: { modo: 'empresa', id: to.params.id } }),
  },
  {
    path: '/personas/:id?',
    redirect: (to) => ({ name: 'Directorio', params: { modo: 'persona', id: to.params.id } }),
  },

  // ====================================================================
  // MÓDULO: Contratación (Mundo 2 — Contractual)
  // ====================================================================

  // Contratos (plantilla Operativo). Lista maestra + detalle.
  {
    path: '/contratos',
    name: 'Contratos',
    component: () => import('@/views/ContratosListView.vue'),
    meta: { requiresAuth: true, layout: 'app', module: 'contratacion' },
    children: [
      {
        path: ':id',
        name: 'ContratoDetalle',
        component: () => import('@/views/ContratoDetalleView.vue'),
        meta: { requiresAuth: true, layout: 'app', module: 'contratacion' },
      },
    ],
  },
  // Pendiente F3: Dashboard de Contratación (requiere endpoint backend de stats).

  // ====================================================================
  // MÓDULO: Facturación (Mundo 3 — Flujo de Facturación)
  // ====================================================================

  // Facturación (plantilla Tablero). Tablero de compromisos del periodo.
  {
    path: '/facturacion',
    name: 'Facturacion',
    component: () => import('@/views/FacturacionView.vue'),
    meta: { requiresAuth: true, layout: 'app', module: 'facturacion' },
  },
  // Pendiente F4: Dashboard de Facturación (Panel sobre /compromisos/vista-periodo).

  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login' });
  } else if (to.name === 'Login' && authStore.isAuthenticated) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});

export default router;
