import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false, layout: 'blank' }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/empresas',
    name: 'Empresas',
    component: () => import('@/views/EmpresasView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/personas',
    name: 'Personas',
    component: () => import('@/views/PersonasView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/pipeline',
    name: 'Pipeline',
    component: () => import('@/views/PipelineView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/oportunidades/:id',
    name: 'OportunidadDetalle',
    component: () => import('@/views/OportunidadDetalleView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navigation guard
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
