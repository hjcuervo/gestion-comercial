import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';

/**
 * meta.layout:
 *  - 'blank'  → sin chasis (Login).
 *  - 'app'    → shell nuevo (AppShell). Pantallas ya migradas al rediseño.
 *  - 'legacy' → pantalla aún no migrada; conserva su propio <AppLayout> (transitorio).
 *
 * A medida que cada pantalla migre (RF2+), se cambia su layout de 'legacy' a 'app'
 * y se le retira el <AppLayout> interno.
 *
 * NOTA: borrar el archivo huérfano router/index.js.old del repo (RF1.8).
 */
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false, layout: 'blank' },
  },

  // --- Pantalla de verificación de la biblioteca base (temporal, quitar antes del merge) ---
  {
    path: '/_kit',
    name: 'ComponentsKit',
    component: () => import('@/views/ComponentsKitView.vue'),
    meta: { requiresAuth: true, layout: 'app' },
  },

  // --- Pantallas aún no migradas (transitorias, layout legacy) ---
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true, layout: 'legacy' },
  },
  {
    path: '/empresas',
    name: 'Empresas',
    component: () => import('@/views/EmpresasView.vue'),
    meta: { requiresAuth: true, layout: 'legacy' },
  },
  {
    path: '/personas',
    name: 'Personas',
    component: () => import('@/views/PersonasView.vue'),
    meta: { requiresAuth: true, layout: 'legacy' },
  },
  {
    path: '/pipeline',
    name: 'Pipeline',
    component: () => import('@/views/PipelineView.vue'),
    meta: { requiresAuth: true, layout: 'legacy' },
  },
  {
    path: '/oportunidades/:id',
    name: 'OportunidadDetalle',
    component: () => import('@/views/OportunidadDetalleView.vue'),
    meta: { requiresAuth: true, layout: 'legacy' },
  },
  {
    path: '/contratos',
    name: 'Contratos',
    component: () => import('@/views/ContratosListView.vue'),
    meta: { requiresAuth: true, layout: 'legacy' },
  },
  {
    path: '/contratos/:id',
    name: 'ContratoDetalle',
    component: () => import('@/views/ContratoDetalleView.vue'),
    meta: { requiresAuth: true, layout: 'legacy' },
  },
  {
    path: '/facturacion',
    name: 'Facturacion',
    component: () => import('@/views/FacturacionView.vue'),
    meta: { requiresAuth: true, layout: 'legacy' },
  },

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
