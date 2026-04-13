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

// Icon names MUST match keys in Icon.vue
const navItems = [
  { path: '/', label: 'Inicio', icon: 'dashboard' },
  { path: '/pipeline', label: 'Pipeline', icon: 'pipeline' },
  { path: '/contratos', label: 'Contratos', icon: 'note-add' },
  { path: '/empresas', label: 'Empresas', icon: 'business' },
  { path: '/personas', label: 'Personas', icon: 'people' },
];

const pageTitle = computed(() => {
  const titles = {
    '/': 'Dashboard',
    '/pipeline': 'Pipeline de Oportunidades',
    '/contratos': 'Contratos',
    '/empresas': 'Empresas',
    '/personas': 'Contactos',
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
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  min-width: 0;
}

.app-layout__content {
  flex: 1;
  padding: var(--space-6);
  background: var(--bg-base);
  overflow: hidden;
  min-width: 0;
}
</style>
