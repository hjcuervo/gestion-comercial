<template>
  <div class="app-layout">
    <NavRail :items="navItems" @logout="handleLogout" />
    
    <div class="app-layout__main">
      <TopAppBar :title="pageTitle" :user-name="userName" />
      
      <main class="app-layout__content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import NavRail from './NavRail.vue';
import TopAppBar from './TopAppBar.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const navItems = [
  { path: '/', label: 'Inicio', icon: 'dashboard' },
  { path: '/pipeline', label: 'Pipeline', icon: 'view_kanban' },
  { path: '/empresas', label: 'Empresas', icon: 'business' },
  { path: '/personas', label: 'Personas', icon: 'people' },
];

const pageTitle = computed(() => {
  const titles = {
    '/': 'Dashboard',
    '/pipeline': 'Pipeline de Oportunidades',
    '/empresas': 'Empresas',
    '/personas': 'Personas',
  };
  return titles[route.path] || route.name || '';
});

const userName = computed(() => authStore.userName);

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
}

.app-layout__main {
  flex: 1;
  margin-left: var(--md-sys-layout-nav-rail-width);
  display: flex;
  flex-direction: column;
}

.app-layout__content {
  flex: 1;
  padding: var(--md-sys-spacing-lg);
  background-color: var(--md-sys-color-background);
}
</style>
