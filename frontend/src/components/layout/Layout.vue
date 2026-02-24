<template>
  <div class="layout">
    <Sidebar 
      :expanded="sidebarExpanded" 
      :user-name="userName"
      :user-role="userRole"
      @toggle="sidebarExpanded = !sidebarExpanded"
      @logout="handleLogout"
    />
    
    <div :class="['layout__main', { 'layout__main--expanded': sidebarExpanded }]">
      <Header 
        :title="pageTitle"
        :breadcrumbs="breadcrumbs"
        :notification-count="3"
      >
        <template #actions>
          <slot name="header-actions" />
        </template>
      </Header>
      
      <main class="layout__content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.store';
import Sidebar from './Sidebar.vue';
import Header from './Header.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const sidebarExpanded = ref(true);

const userName = computed(() => authStore.userName || 'Usuario');
const userRole = computed(() => authStore.userRole || 'Administrador');

const pageTitles = {
  '/': 'Dashboard',
  '/pipeline': 'Pipeline de Oportunidades',
  '/empresas': 'Empresas',
  '/personas': 'Contactos',
  '/actividades': 'Actividades',
  '/reportes': 'Reportes',
};

const pageTitle = computed(() => pageTitles[route.path] || route.name || 'Dashboard');

const breadcrumbs = computed(() => {
  if (route.path === '/') return [];
  
  const crumbs = [];
  const segments = route.path.split('/').filter(Boolean);
  
  let currentPath = '';
  for (const segment of segments) {
    currentPath += `/${segment}`;
    crumbs.push({
      path: currentPath,
      label: pageTitles[currentPath] || segment.charAt(0).toUpperCase() + segment.slice(1)
    });
  }
  
  return crumbs;
});

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.layout__main {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  transition: margin-left var(--duration-base) var(--ease-out);
}

.layout__main--expanded {
  margin-left: var(--sidebar-expanded);
}

.layout__content {
  flex: 1;
  padding: var(--space-6);
  max-width: var(--content-max-width);
  width: 100%;
  margin: 0 auto;
}
</style>
